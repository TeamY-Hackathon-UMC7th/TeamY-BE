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




}
