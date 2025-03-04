package hackathon.spring.web.dto;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
        private Long id;
        private String brand;
        private String coffeeName;
        private String coffeeImgUrl;

        public static CoffeePreviewDTO fromEntity(Coffee coffee) {
            return CoffeePreviewDTO.builder()
                    .id(coffee.getId())
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
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoffeeResponseDto {
        private Long id;
        private String name; // 이름 (NOT NULL)
        private Brand brand;
        private Integer sugar;
        private Integer caffeine;
        private Integer calories;
        private Integer protein;
        private String coffeeImgUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoffeeListResponseDto {
        private List<CoffeeResponseDto> coffeeResponseDtos;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }
}
