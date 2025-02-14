package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.exception.Handler.ReviewHandler;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Review;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.service.ReviewService;
import hackathon.spring.web.dto.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewRestController {

    private final ReviewService reviewService;
    private final MemberRepository memberRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    @Operation(
            summary = "리뷰 작성 API",
            description = """
              어제 먹은 음료에 대해 언제 잠이 들었는지 커멘트와 함께 회고를 하는 리뷰 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<String>> createReview(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid ReviewDto reviewRequestDTO) {

        try {
            String email = reviewService.extractEmailFromToken(token);
            return reviewService.createReview(reviewRequestDTO, email);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", e.getMessage(), null));
        }
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reviewId}")
    @Operation(
            summary = "로그인한 사용자의 리뷰 중 하나 삭제 API입니다.",
            description = """
              로그인 한 사용자가 작성한 리뷰를 삭제하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId) {

        String nickname = reviewService.extractEmailFromToken(token);
        return reviewService.deleteReview(reviewId, nickname);
    }

    @GetMapping("")
    @Operation(
            summary = "로그인한 사용자의 모든 리뷰 가져오기 API입니다.",
            description = """
              로그인 된 사용자가 어제 먹은 음료에 대해 언제 잠이 들었는지 커멘트와 함께 작성한 회고를 조회하는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<List<Review>>> getAllReviews( @RequestHeader("Authorization") String token){
        String email= reviewService.extractEmailFromToken(token);
        Optional<Member> member = memberRepository.findByEmail(email);
        List<Review> allReviews = reviewService.getAllReviews(member.get().getId());

        return ResponseEntity.ok(ApiResponse.onSuccess(allReviews));
    }

}
