package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.service.CoffeeService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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


    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //새로운 음료 등록
    public ApiResponse<Coffee> addCoffee(
                @RequestParam("name") String name,
                @RequestParam("brand") Brand brand,
                @RequestParam("sugar") Integer sugar,
                @RequestParam("caffeine") Integer caffeine,
                @RequestParam("calories") Integer calories,
                @RequestParam("protein") Integer protein,
                @RequestParam("coffeeImg") MultipartFile coffeeImg) {

            Coffee savedCoffee = coffeeService.addCoffee(name, brand, sugar, caffeine, calories, protein, coffeeImg);
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

    @GetMapping("/search") // 검색
    public ApiResponse<Page<Coffee>> searchByKeyword(@RequestParam("keyword") String keyword,
                                                     @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page) {

        Pageable pageable = PageRequest.of(page-1, 5);

        Page<Coffee> coffees = coffeeService.searchByKeyword(keyword, pageable);
        return ApiResponse.onSuccess(coffees);
    }




}
