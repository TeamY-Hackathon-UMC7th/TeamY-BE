package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.service.CoffeeService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/search")
    public ResponseEntity<List<Coffee>> searchByKeyword(@RequestParam("keyword") String keyword) {
        List<Coffee> coffees = coffeeService.searchByKeyword(keyword);
        return ResponseEntity.ok(coffees);
    }




}
