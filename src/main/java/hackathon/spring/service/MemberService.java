package hackathon.spring.service;


import hackathon.spring.global.JwtTokenProvider;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.web.dto.MemberDto;
import hackathon.spring.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public ResponseEntity<ApiResponse> checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            return ResponseEntity.ok(ApiResponse.onSuccess("이미 사용중인 닉네임입니다."));
        }
        return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
    }

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", "이메일은 필수 입력값입니다.", null));
        }

        if (!EmailValidator.isValidEmail(memberDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", "올바른 이메일 형식이 아닙니다.", null));
        }

        if (!PasswordValidator.isValidPassword(memberDto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("400", "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다.", null));
        }

        String email = memberDto.getEmail();
        String nickname = email.substring(0, email.indexOf("@"));

        String encodedPassword = passwordEncoder.encode(memberDto.getPassword());

        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(encodedPassword)
                .nickname(nickname)
                .build();
        memberRepository.save(member);

        MemberDto.JoinResultDto response = MemberDto.JoinResultDto.builder()
                .email(memberDto.getEmail())
                .build();

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    @Transactional
    public ResponseEntity<ApiResponse<Object>> login(MemberDto.LoginRequestDto memberDto) {
        if (memberDto == null || memberDto.getEmail() == null || memberDto.getEmail().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure("400", "이메일은 필수 입력값입니다.", null));
        }

        Optional<Member> member = memberRepository.findByEmail(memberDto.getEmail());
        if (member.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.onFailure("404", "등록된 이메일이 없습니다.", null));
        }

        if (!passwordEncoder.matches(memberDto.getPassword(), member.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", "비밀번호가 틀렸습니다.", null));
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
            return ApiResponse.onSuccess(SuccessStatus._OK,"로그아웃 되었습니다.");
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

        return ApiResponse.onSuccess(SuccessStatus._OK,"회원탈퇴 성공하였습니다.");
    }
}
