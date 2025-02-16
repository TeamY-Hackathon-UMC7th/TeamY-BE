package hackathon.spring.convertor;

import hackathon.spring.domain.Coffee;
import hackathon.spring.web.dto.CoffeeDto;
import org.springframework.stereotype.Component;

@Component
public class CoffeeConverter {

    public CoffeeDto.CoffeeResponseDto toCoffeeDto(Coffee coffee) {
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
}
