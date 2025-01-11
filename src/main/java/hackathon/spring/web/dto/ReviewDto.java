package hackathon.spring.web.dto;

import hackathon.spring.domain.Coffee;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "coffeeKey는 필수 입력값입니다.")
    private Long coffeeKey;

    @Size(max = 200, message = "comment는 최대 200자까지 가능합니다.")
    private String comment;

    @NotNull(message = "drinkTime은 필수입니다.")
    private LocalDateTime drinkTime;

    @NotNull(message = "sleepTime은 필수입니다.")
    private LocalDateTime sleepTime;
}
