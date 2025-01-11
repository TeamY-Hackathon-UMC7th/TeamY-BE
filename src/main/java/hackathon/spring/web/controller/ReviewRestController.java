package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Member;
import hackathon.spring.service.ReviewService;
import hackathon.spring.web.dto.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewRestController {

    private final ReviewService reviewService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
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
            summary = "리뷰 삭제 API"
    )
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable Long reviewId) {

        String nickname = reviewService.extractNicknameFromToken(token);
        return reviewService.deleteReview(reviewId, nickname);
    }

}
