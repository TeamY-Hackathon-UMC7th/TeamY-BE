package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.service.MemberService;
import hackathon.spring.web.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    // 닉네임 중복 체크
    @GetMapping("/check/{nickname}")
    @Operation(
            summary = "닉네임 중복체크 API",
            description = """
               사용자가 원하는 닉네임의 중복된 닉네임이 이미 등록됐는지 확인하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse> checkNickname(@PathVariable String nickname) {
        return memberService.checkNickname(nickname);
    }

    // 회원가입
    @PostMapping("/signup")
    @Operation(
            summary = "회원가입 API",
            description = """
              닉네임 중복체크 후 회원가입을 하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse> signUp(@RequestBody MemberDto.JoinRequestDto memberDto) {
        return memberService.signUp(memberDto);
    }

    // 로그인
    @PostMapping("/login")
    @Operation(
            summary = "로그인 API"
    )
    public ResponseEntity<ResponseEntity<ApiResponse>> login(@RequestBody MemberDto.LoginRequestDto memberDto) {
        return memberService.login(memberDto);
    }
}
