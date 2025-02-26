package hackathon.spring.service;


import hackathon.spring.global.JwtTokenProvider;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.web.dto.MemberDto;
import hackathon.spring.domain.Member;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;


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
    public MemberDto.JoinResultDto signUp(MemberDto.JoinRequestDto memberDto) {

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

        return response;
    }

    // 인증코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999 범위의 숫자 생성
        return String.valueOf(code);
    }

    // 인증코드 이메일로 전송
    private void sendVerificationEmail(String toEmail, String verificationCode) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("회원가입 이메일 인증 코드");
            helper.setText("인증 코드: " + verificationCode + "\n이 코드는 5분 동안 유효합니다.", false);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    public MemberDto.EmailResultDto sendVerificationCode(String email) {

         if(memberRepository.existsByEmail(email)) {
             MemberDto.EmailResultDto response = MemberDto.EmailResultDto.builder()
                     .canUse(false)
                     .code(null)
                     .build();

             return response;
         }

        String verificationCode = generateVerificationCode(); // 인증 코드 생성
        sendVerificationEmail(email, verificationCode); // 이메일 발송

        MemberDto.EmailResultDto response = MemberDto.EmailResultDto.builder()
                .canUse(true)
                .code(verificationCode)
                .build();

        return response;

    }

    @Transactional
    public ResponseEntity<ApiResponse<Object>> login(MemberDto.LoginRequestDto memberDto) {
        if (memberDto == null || memberDto.getEmail() == null || memberDto.getEmail().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_EMAIL);
        }

        Member member = memberRepository.findByEmail(memberDto.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())) {
            throw new GeneralException(ErrorStatus._INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getEmail());

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
                .id(member.getId())
                .email(member.getEmail())
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

    @Transactional
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

        return ApiResponse.onSuccess(SuccessStatus._OK, (Object) "비밀번호가 변경되었습니다.");
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateNickname(String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (nickname == null || nickname.trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_NICKNAME);
        }

        if(nickname.length()>10){
            throw new GeneralException(ErrorStatus._INVALID_NICKNAME_FORMAT);
        }

        member.setNickname(nickname);

        return ApiResponse.onSuccess(SuccessStatus._OK, (Object) "닉네임이 변경되었습니다.");
    }

    @Transactional
    public ApiResponse<String> notifyAlarm(Boolean notification, Long userId) {
        Member member = memberRepository.findById(userId).get();
        member.setNotification(notification);
        memberRepository.save(member);
        return ApiResponse.onSuccess("알림 설정에 성공하였습니다. 알림 설정: " + notification);
    }
}
