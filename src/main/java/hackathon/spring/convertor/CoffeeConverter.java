package hackathon.spring.convertor;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Recommendation;
import hackathon.spring.web.dto.CoffeeDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CoffeeConverter {

    public static CoffeeDto.CoffeeResponseDto toCoffeeDto(Coffee coffee) {
        return CoffeeDto.CoffeeResponseDto.builder()
                .id(coffee.getId())
                .name(coffee.getName())
                .brand(coffee.getBrand())
                .coffeeImgUrl(coffee.getCoffeeImgUrl())
                .sugar(coffee.getSugar())
                .caffeine(coffee.getCaffeine())
                .calories(coffee.getCalories())
                .protein(coffee.getProtein())
                .build();
    }

    public static CoffeeDto.CoffeeListResponseDto toCoffeeListDto(Page<Coffee> coffeePage) {
        List<CoffeeDto.CoffeeResponseDto> coffeeDtos = coffeePage.stream().map(CoffeeConverter::toCoffeeDto).toList();

        return CoffeeDto.CoffeeListResponseDto.builder()
                .coffeeResponseDtos(coffeeDtos)
                .currentPage(coffeePage.getNumber())  // 현재 페이지
                .totalPages(coffeePage.getTotalPages())  // 전체 페이지 수
                .totalElements(coffeePage.getTotalElements())  // 전체 데이터 개수
                .isFirst(coffeePage.isFirst())  // 첫 페이지 여부
                .isLast(coffeePage.isLast())  // 마지막 페이지 여부
                .pageSize(coffeePage.getSize())  // 한 페이지당 개수
                .build();
    }



    public static CoffeeDto.CoffeePreviewDTO toPreviewDTO(Coffee coffee) {
        return new CoffeeDto.CoffeePreviewDTO(
                coffee.getId(),
                coffee.getBrand().name(),
                coffee.getName(),
                coffee.getCoffeeImgUrl()
        );
    }

    public static CoffeeDto.CoffeePreviewDTO toPreviewDTO(Recommendation recommendation) {
        return new CoffeeDto.CoffeePreviewDTO(
                recommendation.getCoffee().getId(),
                recommendation.getCoffee().getBrand().name(),
                recommendation.getCoffee().getName(),
                recommendation.getCoffee().getCoffeeImgUrl()
        );
    }
}
