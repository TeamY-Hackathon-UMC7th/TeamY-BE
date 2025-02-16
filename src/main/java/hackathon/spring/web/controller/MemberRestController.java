package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.service.MemberService;
import hackathon.spring.web.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;

    // 닉네임 중복 체크
//    @GetMapping("/check/{nickname}")
//    @Operation(
//            summary = "닉네임 중복 체크 API",
//            description = """
//             닉네임 중복을 체크하는 API입니다.
//                """
//    )
//    public ResponseEntity<ApiResponse> checkNickname(@PathVariable String nickname) {
//        return memberService.checkNickname(nickname);
//    }

    // 회원가입
    @PostMapping("/join")
    @Operation(
            summary = "회원가입 API",
            description = """
              닉네임으로 회원가입하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse> signUp(@RequestBody MemberDto.JoinRequestDto memberDto) {
        return memberService.signUp(memberDto);
    }

    // 로그인
    @PostMapping("/login")
    @Operation(
            summary = "로그인 API",
            description = """
              로그인하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody MemberDto.LoginRequestDto memberDto) {
        return memberService.login(memberDto);
    }

    // 로그아웃
    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API"
    )
    public ResponseEntity<ApiResponse> logout() {
        return memberService.logout();
    }

    // 회원탈퇴
    @Operation(
            summary = "회원탈퇴 API",
            description = """
              회원탈퇴하는 API입니다.
                """
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteMember() {
        return memberService.deleteMember();
    }

    // 액세스토큰 재발급
    @PostMapping("/reissue")
    @Operation(
            summary = "access 토큰 재발급 API",
            description = """
              refresh토큰으로 access토큰 재발급받는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<Object>> refresh() {
        return memberService.refresh();
    }

    @PutMapping("/password/update")
    @Operation(
            summary = "비밀번호 변경 API",
            description = """
              비밀번호 변경하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody MemberDto.PasswordChangeRequestDto passwordDto) {
        return memberService.updatePassword(passwordDto);
    }


}