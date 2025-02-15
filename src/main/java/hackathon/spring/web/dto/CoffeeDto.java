package hackathon.spring.web.dto;

import hackathon.spring.domain.Coffee;
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

}
