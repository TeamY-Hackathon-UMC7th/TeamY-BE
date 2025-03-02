package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.service.MemberService;
import hackathon.spring.service.MyPageService;
import hackathon.spring.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    private final MemberService memberService;

    private String extractMemberNickname() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 추출된 사용자 이메일 또는 ID
        return myPageService.getMemberNicknameByEmail(email); // 이메일을 기반으로 memberId 가져오기
    }

    private Long extractMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 추출된 사용자 이메일 또는 ID
        return memberService.getMemberIdByEmail(email); // 이메일을 기반으로 memberId 가져오기
    }

    //닉네임 get
    @GetMapping("/nickname")
    @Operation(summary = "닉네임 반환 API", description = "현재 접속 사용자의 닉네임을 조회합니다.")
    public ResponseEntity<ApiResponse<String>> getNickname() {
        String memberNickname = extractMemberNickname();
        return ResponseEntity.ok(ApiResponse.onSuccess(memberNickname));
    }

    // 커피기록개수 get
    @GetMapping("/howmanycoffee")
    @Operation(summary = "커피기록개수 API", description = "현재 접속 사용자의 커피 기록 개수를 조회합니다.")
    public ResponseEntity<ApiResponse<Integer>> getCoffeeRecordNum() {
        Long memberId = extractMemberId();
        return ResponseEntity.ok(ApiResponse.onSuccess(myPageService.getCoffeeRecordNum(memberId)));
    }


    // 전체 추천 기록 get
}
