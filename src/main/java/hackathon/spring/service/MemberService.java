package hackathon.spring.service;


import hackathon.spring.global.JwtTokenProvider;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.web.dto.MemberDto;
import hackathon.spring.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    // 닉네임 중복 가능해서 사용 X
//    public ResponseEntity<ApiResponse> checkNickname(String nickname) {
//        if (memberRepository.existsByNickname(nickname)) {
//            return ResponseEntity.ok(ApiResponse.onSuccess("이미 사용중인 닉네임입니다."));
//        }
//        return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
//    }

    private class PasswordValidator {
        private static final String PASSWORD_PATTERN =
                "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

        private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

        public static boolean isValidPassword(String password) {
            return pattern.matcher(password).matches();
        }
    }

    private class EmailValidator {
        private static final String EMAIL_REGEX =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

        public static boolean isValidEmail(String email) {
            return email != null && EMAIL_PATTERN.matcher(email).matches();
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse> signUp(MemberDto.JoinRequestDto memberDto) {

        if (memberDto == null || memberDto.getEmail() == null || memberDto.getEmail().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_EMAIL);
        }

        if (memberDto.getPassword() == null || memberDto.getPassword().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_PASSWORD);
        }

        String email = memberDto.getEmail();
        String password = memberDto.getPassword();

        if (!EmailValidator.isValidEmail(email)) {
            throw new GeneralException(ErrorStatus._INVALID_EMAIL_FORMAT);
        }

        if (memberRepository.existsByEmail(email))
            throw new GeneralException(ErrorStatus._DUPLICATE_EMAIL);

        if (!PasswordValidator.isValidPassword(password)) {
            throw new GeneralException(ErrorStatus._INVALID_PASSWORD_FORMAT);
        }

        if (!Objects.equals(memberDto.getCheckPassword(), password)){
            throw new GeneralException(ErrorStatus._NOT_MATCH_PASSWORD);
        }

        String nickname = email.substring(0, email.indexOf("@"));
        String encodedPassword = passwordEncoder.encode(memberDto.getPassword());

        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(encodedPassword)
                .nickname(nickname)
                .notification(false)
                .build();
        memberRepository.save(member);

        MemberDto.JoinResultDto response = MemberDto.JoinResultDto.builder()
                .id(member.getId())
                .email(memberDto.getEmail())
                .build();

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<Object>> login(MemberDto.LoginRequestDto memberDto) {
        if (memberDto == null || memberDto.getEmail() == null || memberDto.getEmail().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_EMAIL);
        }

        Optional<Member> member = memberRepository.findByEmail(memberDto.getEmail());
        if (member.isEmpty()) {
            throw new GeneralException(ErrorStatus._NOT_REGISTERED_USER);
        }

        if (!passwordEncoder.matches(memberDto.getPassword(), member.get().getPassword())) {
            throw new GeneralException(ErrorStatus._INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.get().getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.get().getEmail());

        // Set-Cookie 헤더로 토큰 저장
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)  // HTTPS 환경에서만
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiration() / 1000)
                .build();

        MemberDto.LoginResultDto response = MemberDto.LoginResultDto.builder()
                .id(member.get().getId())
                .email(member.get().getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenExpiration())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.onSuccess(SuccessStatus._OK, response).getBody());
    }

    @Transactional
    public ResponseEntity<ApiResponse> logout() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new GeneralException(ErrorStatus._INVALID_TOKEN); // 인증 정보가 없을 경우 예외 발생
        }

        String token = authentication.getCredentials().toString();

        if (jwtTokenProvider.validateToken(token)) {
            long expiration = jwtTokenProvider.getExpiration(token); // 토큰 만료 시간 가져오기
            jwtTokenProvider.addToBlacklist(token, expiration);
            return ApiResponse.onSuccess(SuccessStatus._OK, Optional.of("로그아웃 되었습니다."));
        }

        throw new GeneralException(ErrorStatus._INVALID_TOKEN);
    }

    @Transactional
    public ResponseEntity<ApiResponse> deleteMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String token = authentication.getCredentials().toString();

        long expiration = jwtTokenProvider.getExpiration(token);
        jwtTokenProvider.addToBlacklist(token, expiration);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
        memberRepository.delete(member);

        return ApiResponse.onSuccess(SuccessStatus._OK, (Object) "회원탈퇴 성공하였습니다.");
    }

    @Transactional
    public  ResponseEntity<ApiResponse<Object>> refresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String token = authentication.getCredentials().toString();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        String accessToken = jwtTokenProvider.generateAccessToken(member.getEmail());

        // Set-Cookie 헤더로 토큰 저장
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)  // HTTPS 환경에서만
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExpiration() / 1000)
                .build();

        MemberDto.LoginResultDto response = MemberDto.LoginResultDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .accessToken(accessToken)
                .refreshToken(token)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenExpiration())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.onSuccess(SuccessStatus._OK, response).getBody());
    }

    public ResponseEntity<ApiResponse> updatePassword(MemberDto.PasswordChangeRequestDto passwordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        String storedPassword = member.getPassword(); // DB에 저장된 해싱된 비밀번호
        String currentPassword = passwordDto.getCurrentPassword();
        String updatePassword = passwordDto.getUpdatePassword();

        // 입력한 비밀번호와 비교
        if(!passwordEncoder.matches(currentPassword, storedPassword)){
            throw new GeneralException(ErrorStatus._INVALID_PASSWORD);
        }
        if (passwordEncoder.matches(updatePassword, storedPassword)) {
            throw new GeneralException(ErrorStatus._NOT_CHANGE_PASSWORD);
        }

        if (!PasswordValidator.isValidPassword(updatePassword)) {
            throw new GeneralException(ErrorStatus._INVALID_PASSWORD_FORMAT);
        }

        if(!Objects.equals(passwordDto.getCheckPassword(), updatePassword)){
            throw new GeneralException(ErrorStatus._NOT_MATCH_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(updatePassword);

        member.setPassword(encodedPassword);

        // DB에 저장
        memberRepository.save(member);

        return ApiResponse.onSuccess(SuccessStatus._OK, (Object) "비밀번호가 변경되었습니다.");
    }

    public ApiResponse<String> notifyAlarm(Boolean notification, Long userId) {
        Member member = memberRepository.findById(userId).get();
        member.setNotification(notification);
        memberRepository.save(member);
        return ApiResponse.onSuccess("알림 설정에 성공하였습니다. 알림 설정: " + notification);
    }
}
