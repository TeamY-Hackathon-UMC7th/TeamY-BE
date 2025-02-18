package hackathon.spring.web.dto;

import hackathon.spring.domain.Coffee;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoffeeDto {
    private List<Coffee> coffees;

    /**
     * 커피 정보를 표현하는 DTO
     * 사용 위치: NotePreviewDTO, NoteDTO 내부
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoffeePreviewDTO {
        private String brand;
        private String coffeeName;
        private String coffeeImgUrl;

        public static CoffeePreviewDTO fromEntity(Coffee coffee) {
            return CoffeePreviewDTO.builder()
                    .coffeeName(coffee.getName())
                    .brand(coffee.getBrand().name()) // Enum 변환
                    .coffeeImgUrl(coffee.getCoffeeImgUrl())
                    .build();
        }
    }

    /**
     * 커피 영양성분까지 정보를 표현하는 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoffeeDetailPreviewDTO {
        private String coffeeName;
        private String brand;
        private Integer caffeine;
        private Integer drinkCount;
        private String coffeeImgUrl;

        public static CoffeeDetailPreviewDTO fromEntity(Coffee coffee) {
            return CoffeeDetailPreviewDTO.builder()
                    .coffeeName(coffee.getName())
                    .brand(coffee.getBrand().name()) // Enum 변환
                    .caffeine(coffee.getCaffeine())
                    .drinkCount(coffee.getDrinkCount())
                    .coffeeImgUrl(coffee.getCoffeeImgUrl())
                    .build();
        }
    }
}
