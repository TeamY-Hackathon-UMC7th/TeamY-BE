package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Review;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.service.ReviewService;
import hackathon.spring.web.dto.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
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
              어제 먹은 음료에 대해 언제 잠이 들었는지 회고를 하는 리뷰 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<String>> createReview(
            @RequestHeader("Authorization") String token,
            @RequestBody ReviewDto reviewRequestDTO) {

        try {
            String nickname = reviewService.extractNicknameFromToken(token);
            return reviewService.createReview(reviewRequestDTO, nickname);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", e.getMessage(), null));
        }
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{reviewId}")
    @Operation(
            summary = "로그인한 사용자의 리뷰 중 하나 삭제 API입니다."
    )
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId) {

        String nickname = reviewService.extractNicknameFromToken(token);
        return reviewService.deleteReview(reviewId, nickname);
    }

    @GetMapping("")
    @Operation(
            summary = "로그인한 사용자의 모든 리뷰 가져오기 API입니다."
    )
    public ResponseEntity<ApiResponse<Optional<Review>>> getAllReviews( @RequestHeader("Authorization") String token){
        String nickname = reviewService.extractNicknameFromToken(token);
        Optional<Member> member = memberRepository.findByNickname(nickname);
        Optional<Review> allReviews = reviewService.getAllReviews(member.get().getId());

        if (allReviews.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.onSuccess( Optional.empty(), "No reviews found for the given member.")
            );
        }
        return ResponseEntity.ok(ApiResponse.onSuccess(allReviews));
    }

}
