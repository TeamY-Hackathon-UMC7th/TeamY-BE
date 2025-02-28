package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Member;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.service.MemberService;
import hackathon.spring.service.NoteService;
import hackathon.spring.web.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final NoteService noteService;

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

    @PostMapping("/email")
    @Operation(
            summary = "이메일 인증 API",
            description = """
              이메일로 인증번호를 받는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse> verifyCode(@RequestParam String email) {
        return memberService.sendVerificationCode(email);
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

    // 알림 동의
    @PostMapping("/alarm/{notification}")
    @Operation(
            summary = "알림 동의 API",
            description = """
              알림 동의여부를 체크하는 API입니다.
                """
    )
    public ApiResponse<String> notifyAlarm(@PathVariable Boolean notification) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 추출된 사용자 이메일 또는 ID
        Long userId = noteService.getMemberIdByEmail(email);

        return memberService.notifyAlarm(notification, userId);
    }

    @PostMapping("/nickname/update")
    @Operation(
            summary = "닉네임 변경 API",
            description = """
              닉네임을 변경하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse> updateNickname(@RequestParam String nickname) {
        return memberService.updateNickname(nickname);
    }

    @GetMapping("/user")
    @Operation(
            summary = "회원정보 반환 API",
            description = """
              회원 정보를 반환하는 API입니다.
                """
    )
    public ApiResponse<Member> returnUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 추출된 사용자 이메일 또는 ID
        Member member = memberRepository.findMemberByEmail(email);
        return ApiResponse.onSuccess(member);
    }
}