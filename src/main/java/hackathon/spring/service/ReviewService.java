package hackathon.spring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Review;
import hackathon.spring.repository.CoffeeRepository;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.repository.ReviewRepository;
import hackathon.spring.web.dto.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final CoffeeRepository coffeeRepository;

    public String extractNicknameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token is missing or improperly formatted");
        }

        String jwtToken = token.replace("Bearer ", "");
        try {
            return JWT.require(Algorithm.HMAC512("${SECRET_KEY}"))
                    .build()
                    .verify(jwtToken)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> createReview(ReviewDto dto, String nickname) {
        Optional<Member> memberOptional = memberRepository.findByNickname(nickname);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            Coffee coffee = coffeeRepository.findById(dto.getCoffeeKey())
                    .orElseThrow(() -> new RuntimeException("Coffee not found"));

            if (dto.getComment().length() > 200) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure("400", "Comment cannot exceed 200 characters.", null));
            }

            Review review = Review.builder()
                    .member(member)
                    .coffee(coffee)
                    .drinkTime(dto.getDrinkTime())
                    .sleepTime(dto.getSleepTime())
                    .comment(dto.getComment())
                    .build();

            reviewRepository.save(review);
            return ResponseEntity.ok(ApiResponse.onSuccess("Review created successfully!"));
        } else {
            return ResponseEntity.status(ErrorStatus._UNAUTHORIZED.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus._UNAUTHORIZED.getCode(),
                            ErrorStatus._UNAUTHORIZED.getMessage(),
                            null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteReview(Long reviewId, String nickname) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._REVIEW_NOT_FOUND));

        if (!review.getMember().getNickname().equals(nickname)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.onFailure("401", "You can only delete your own reviews.", null));
        }

        reviewRepository.delete(review);
        return ResponseEntity.ok(ApiResponse.onSuccess("Review deleted successfully!"));
    }

    @Transactional
    public List<Review> getAllReviews(){
        List<Review> reviews = reviewRepository.findAll();
        return reviews;
    }

}
