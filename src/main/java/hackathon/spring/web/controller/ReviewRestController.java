package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Member;
import hackathon.spring.service.ReviewService;
import hackathon.spring.web.dto.ReviewDto;
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

}