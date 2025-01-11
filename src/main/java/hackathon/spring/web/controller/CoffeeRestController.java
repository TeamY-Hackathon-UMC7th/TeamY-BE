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




}
