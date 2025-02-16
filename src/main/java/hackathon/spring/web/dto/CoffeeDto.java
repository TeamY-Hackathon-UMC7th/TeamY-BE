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

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoffeeDto {
    private List<Coffee> coffees;

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
}
