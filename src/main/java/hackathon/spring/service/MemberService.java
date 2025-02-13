package hackathon.spring.service;


import hackathon.spring.JwtTokenProvider;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.web.dto.MemberDto;
import hackathon.spring.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

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

    public ResponseEntity<ApiResponse> signUp(MemberDto.JoinRequestDto memberDto) {
        if (memberDto == null || memberDto.getNickname() == null || memberDto.getNickname().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_NICKNAME);
        }

        if (memberRepository.existsByNickname(memberDto.getNickname())) {
            throw new GeneralException(ErrorStatus._DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(memberDto.getPassword());

        Member member = Member.builder()
                .nickname(memberDto.getNickname())
                .password(encodedPassword)
                .build();
        memberRepository.save(member);

        MemberDto.JoinResultDto response = MemberDto.JoinResultDto.builder()
                .nickname(memberDto.getNickname())
                .build();

        return ApiResponse.onSuccess(SuccessStatus._OK, response);
    }

    public ResponseEntity<ResponseEntity<ApiResponse>> login(MemberDto.LoginRequestDto memberDto) {
        if (memberDto == null || memberDto.getNickname() == null || memberDto.getNickname().trim().isEmpty()) {
            throw new GeneralException(ErrorStatus._EMPTY_NICKNAME);
        }

        Member member = memberRepository.findByNickname(memberDto.getNickname())
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())) {
            throw new GeneralException(ErrorStatus._LOGIN_FAILED);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getNickname());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getNickname());

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
                .nickname(member.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .refreshTokenExpiresIn(jwtTokenProvider.getRefreshTokenExpiration())
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", accessCookie.toString())
                .header("Set-Cookie", refreshCookie.toString())
                .body(ApiResponse.onSuccess(SuccessStatus._OK, response));
    }
}
