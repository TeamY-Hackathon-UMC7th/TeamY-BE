package hackathon.spring.web.controller;

import hackathon.spring.domain.Coffee;
import hackathon.spring.service.CoffeeService;
import hackathon.spring.web.dto.TimeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CoffeeRestController {
    private final CoffeeService coffeeService;

    @PostMapping("/coffee/recommend")
    public ResponseEntity<List<Coffee>> recommendCoffees(@RequestBody TimeRequestDto timeRequestDto) {
        List<Coffee> recommendedCoffees = coffeeService.recommendByCaffeineLimit(timeRequestDto.getUserTimeInput());
        return ResponseEntity.ok(recommendedCoffees);
    }
}
