package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.service.MemberService;
import hackathon.spring.web.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    // 닉네임 중복 체크
    @GetMapping("/check/{nickname}")
    public ResponseEntity<ApiResponse<String>> checkNickname(@PathVariable String nickname) {
        return memberService.checkNickname(nickname);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signUp(@RequestBody MemberDto.JoinResultDto memberDto) {
        return memberService.signUp(memberDto);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody MemberDto.JoinResultDto memberDto) {
        return memberService.login(memberDto);
    }
}
