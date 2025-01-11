package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Review;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.service.CoffeeService;
import hackathon.spring.web.dto.CoffeeDto;
import hackathon.spring.web.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
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


//    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //새로운 음료 등록
//    public ApiResponse<Coffee> addCoffee(
//                @RequestParam("name") String name,
//                @RequestParam("brand") Brand brand,
//                @RequestParam("sugar") Integer sugar,
//                @RequestParam("caffeine") Integer caffeine,
//                @RequestParam("calories") Integer calories,
//                @RequestParam("protein") Integer protein,
//                @RequestParam("coffeeImg") MultipartFile coffeeImg) {
//
//            Coffee savedCoffee = coffeeService.addCoffee(name, brand, sugar, caffeine, calories, protein, coffeeImg);
//        return ApiResponse.onSuccess(savedCoffee);
//    }


    @GetMapping("/recommend")
    @Operation(
            summary = "카페인 농도에 따라 음료 추천 API",
            description = """
               사용자가 원하는 시간에 잘 수 있게 하는 카페인의 양을 가진 카페 음료를 추천해주는 API입니다.
                - 요청 본문에는 userTimeInput, 즉 사용자가 자고싶은 시간이 포함되어야 합니다.
                """
    )
    public ResponseEntity<ApiResponse<CoffeeDto>> recommendCoffees(@RequestParam("time") @Max(24)String time) {
        return coffeeService.recommendByCaffeineLimit(time);
    }

    @GetMapping("/popular")
    @Operation(
            summary = "인기메뉴 추천 API",
            description = """
              랜덤으로 인기 있는 음료 5개를 추천해주는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<CoffeeDto>> getPopularCoffees() {
        return coffeeService.recommendPopularCoffees();
    }

    @GetMapping("/search")
    @Operation(
            summary = "음료 검색 API",
            description = """
             브랜드명이나 음료 이름으로 검색하는 API입니다. ex.스타벅스 , 아메리카노, 스타벅스 아메리카노
                """
    )
    public ResponseEntity<List<Coffee>> searchByKeyword(@RequestParam("keyword") String keyword) {
        List<Coffee> coffees = coffeeService.searchByKeyword(keyword);
        return ResponseEntity.ok(coffees);
    }






}
