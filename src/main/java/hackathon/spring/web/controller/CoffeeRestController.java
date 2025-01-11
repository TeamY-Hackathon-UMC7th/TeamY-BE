package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Coffee;
import hackathon.spring.service.CoffeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import hackathon.spring.web.dto.TimeRequestDto;
import org.springframework.web.bind.annotation.*;
  
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/coffee")
public class CoffeeRestController {
    private final CoffeeService coffeeService;

    @PostMapping("/add") //새로운 음료 등록
    public ApiResponse<Coffee> addCoffee(@RequestBody Coffee coffee) {
        Coffee savedCoffee = coffeeService.addCoffee(coffee);
        return ApiResponse.onSuccess(savedCoffee);
    }
  
  
    @PostMapping("/recommend") // 카페인 농도에 따라 음료 추천
    public ResponseEntity<List<Coffee>> recommendCoffees(@RequestBody TimeRequestDto timeRequestDto) {
        List<Coffee> recommendedCoffees = coffeeService.recommendByCaffeineLimit(timeRequestDto.getUserTimeInput());
        return ResponseEntity.ok(recommendedCoffees);
    }

    @GetMapping("/popular") // 인기메뉴 추천
    public List<Coffee> getPopularCoffees() {
        return coffeeService.recommendPopularCoffees();
    }





}
