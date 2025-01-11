package hackathon.spring.service;


import hackathon.spring.JwtTokenProvider;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
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

    public ResponseEntity<ApiResponse<String>> checkNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            return ResponseEntity
                    .status(ErrorStatus._DUPLICATE_NICKNAME.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._DUPLICATE_NICKNAME.getCode(),
                            ErrorStatus._DUPLICATE_NICKNAME.getMessage(),
                            null));
        }
        return ResponseEntity.ok(ApiResponse.onSuccess("사용 가능한 닉네임입니다."));
    }

    public ResponseEntity<ApiResponse<String>> signUp(MemberDto.JoinResultDto memberDto) {
        if (memberRepository.existsByNickname(memberDto.getNickname())) {
            return ResponseEntity
                    .status(ErrorStatus._BAD_REQUEST.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._BAD_REQUEST.getCode(),
                            ErrorStatus._BAD_REQUEST.getMessage(),
                            null));
        }

        Member member = Member.builder()
                .nickname(memberDto.getNickname())
                .build();
        memberRepository.save(member);

        String token = JwtTokenProvider.generateToken(memberDto.getNickname());
        return ResponseEntity.ok(ApiResponse.onSuccess(token));
    }

    public ResponseEntity<ApiResponse<String>> login(MemberDto.JoinResultDto memberDto) {
        Optional<Member> member = memberRepository.findByNickname(memberDto.getNickname());

        if (member.isPresent()) {
            String token = JwtTokenProvider.generateToken(memberDto.getNickname());
            return ResponseEntity.ok(ApiResponse.onSuccess(token));
        } else {
            return ResponseEntity
                    .status(ErrorStatus._UNAUTHORIZED.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._UNAUTHORIZED.getCode(),
                            ErrorStatus._UNAUTHORIZED.getMessage(),
                            null));
        }
    }
}
