package hackathon.spring.service;


import hackathon.spring.JwtTokenProvider;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.web.dto.MemberDto;
import hackathon.spring.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MemberService {
    private final MemberRepository memberRepository;

    public ResponseEntity<ApiResponse<MemberDto.JoinResponseDto>> checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {

            MemberDto.JoinResponseDto joinResponseDto = MemberDto.JoinResponseDto.builder()
                    .status(false)
                    .build();

            return ResponseEntity
                    .status(SuccessStatus._OK.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._DUPLICATE_NICKNAME.getCode(),
                            ErrorStatus._DUPLICATE_NICKNAME.getMessage(),
                            joinResponseDto));
        }

        MemberDto.JoinResponseDto joinResponseDto = MemberDto.JoinResponseDto.builder()
                .status(true)
                .build();
        return ResponseEntity
                .status(SuccessStatus. _AVAILABLE_NICKNAME.getHttpStatus())
                .body(ApiResponse.onSuccess(
                        SuccessStatus._AVAILABLE_NICKNAME.getCode(),
                        SuccessStatus. _AVAILABLE_NICKNAME.getMessage(),
                        joinResponseDto));
    }

    public ResponseEntity<ApiResponse<MemberDto.JoinResponseDto>> signUp(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            ResponseEntity.status(ErrorStatus._EMPTY_NICKNAME.getHttpStatus()).body("닉네임은 빈 값일 수 없습니다.");
        }
        if (memberRepository.existsByNickname(nickname)) {
            return ResponseEntity
                    .status(ErrorStatus._DUPLICATE_NICKNAME.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._DUPLICATE_NICKNAME.getCode(),
                            ErrorStatus._DUPLICATE_NICKNAME.getMessage(),
                            null));
        }

        Member member = Member.builder()
                .nickname(nickname)
                .build();
        memberRepository.save(member);

        return ResponseEntity
                .status(SuccessStatus. _SIGNUP_SUCCESS.getHttpStatus())
                .body(ApiResponse.onSuccess(
                        SuccessStatus._SIGNUP_SUCCESS.getCode(),
                        SuccessStatus. _SIGNUP_SUCCESS.getMessage(),
                        null));

    }

    public ResponseEntity<ApiResponse<MemberDto.LoginResponseDto>> login(String nickname) {
        Optional<Member> member = memberRepository.findByNickname(nickname);

        if (member.isPresent()) {
            String token = JwtTokenProvider.generateToken(nickname);
            MemberDto.LoginResponseDto joinResponseDto = MemberDto.LoginResponseDto.builder()
                    .nickname(nickname)
                    .token(token)
                    .build();
            return ResponseEntity
                    .status(SuccessStatus._LOGIN_SUCCESS.getHttpStatus())
                    .body(ApiResponse.onSuccess(
                            SuccessStatus._LOGIN_SUCCESS.getCode(),
                            SuccessStatus._LOGIN_SUCCESS.getMessage(),
                            joinResponseDto));
        } else {

            return ResponseEntity
                    .status(ErrorStatus._NOT_REGISTERED_USER.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._NOT_REGISTERED_USER.getCode(),
                            ErrorStatus._NOT_REGISTERED_USER.getMessage(),
                            null));
        }
    }
}