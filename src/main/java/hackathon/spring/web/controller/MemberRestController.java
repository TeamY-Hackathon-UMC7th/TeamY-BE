package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.service.MemberService;
import hackathon.spring.web.dto.MemberDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    // 닉네임 중복 체크
    @GetMapping("/check/{nickname}")
    public ResponseEntity<ApiResponse<MemberDto.JoinResponseDto>> checkNickname(@PathVariable
                                                             @NotNull @NotBlank
                                                             @Size(max=20) String nickname) {
        return memberService.checkNickname(nickname);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberDto.JoinResponseDto>> signUp(@RequestParam
                                                      @NotNull @NotBlank
                                                      @Size(max=20)String nickname) {
        return memberService.signUp(nickname);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestParam
                                                     @NotNull @NotBlank
                                                     @Size(max=20)String nickname) {
        return memberService.login(nickname);
    }
}