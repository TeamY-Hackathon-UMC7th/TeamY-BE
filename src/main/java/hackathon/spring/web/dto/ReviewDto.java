package hackathon.spring.web.dto;

import hackathon.spring.domain.Coffee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long coffeeKey;              // 음료 키
    private LocalDateTime drinkTime;     // 음용 시간
    private LocalDateTime sleepTime;     // 수면 시간
    private String comment;              // 코멘트 (200자 제한)
}
