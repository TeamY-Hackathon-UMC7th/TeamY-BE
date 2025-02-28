package hackathon.spring.convertor;

import hackathon.spring.domain.Coffee;
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

    public static CoffeeDto.CoffeeListResponseDto toCoffeeListDto(Page<Coffee> coffees) {
        List<CoffeeDto.CoffeeResponseDto> coffeeDtos = coffees.stream().map(CoffeeConverter::toCoffeeDto).toList();

        return CoffeeDto.CoffeeListResponseDto.builder()
                .coffeeResponseDtos(coffeeDtos)
                .isFirst(coffees.isFirst())
                .isLast(coffees.isLast())
                .listSize(coffeeDtos.size())
                .totalElements(coffees.getTotalElements())
                .totalPage(coffees.getTotalPages())
                .build();
    }
}
