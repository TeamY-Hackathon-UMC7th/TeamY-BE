package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.convertor.CoffeeConverter;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.service.CoffeeService;
import hackathon.spring.service.MemberService;
import io.swagger.v3.oas.annotations.Parameter;
import hackathon.spring.web.dto.CoffeeDto;
import hackathon.spring.web.dto.MemberDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/coffee")
public class CoffeeRestController {
    private final CoffeeService coffeeService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private String extractMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 추출된 사용자 이메일 또는 ID
        return email; // 이메일을가져오기
    }


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
    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeePreviewDTO>>> recommendCoffees(@RequestParam("time") @Max(24)Integer time) {
        String email = extractMemberEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_REGISTERED_USER));
        return coffeeService.recommendByCaffeineLimit(member, time);
    }

    @GetMapping("/{coffeeId}")
    @Operation(
            summary = "카페인 농도에 따라 음료 추천 API",
            description = """
               사용자가 원하는 시간에 잘 수 있게 하는 카페인의 양을 가진 카페 음료를 추천해주는 API입니다.
                - 요청 본문에는 userTimeInput, 즉 사용자가 자고싶은 시간이 포함되어야 합니다.
                """
    )
    public ResponseEntity<ApiResponse<CoffeeDto.CoffeeResponseDto>> findCoffee(@PathVariable Long coffeeId) {
        return coffeeService.getCoffeeInfo(coffeeId);
    }

    @GetMapping("/popular")
    @Operation(
            summary = "인기메뉴 추천 API",
            description = """
              사람들이 많이 기록한 인기 있는 음료 5개를 추천해주는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeeDetailPreviewDTO>>> getPopularCoffees() {
        return coffeeService.getPopularCoffees();
    }

    @GetMapping("/search") // 검색
    @Operation(
            summary = "음료 검색 API",
            description = """
             브랜드명이나 음료 이름으로 검색하는 API입니다. ex.스타벅스 , 아메리카노
                """
    )
    public ResponseEntity<ApiResponse<CoffeeDto.CoffeeListResponseDto>> searchByKeyword(@RequestParam("keyword") String keyword,
                                                                        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") Integer page) {

        Pageable pageable = PageRequest.of(page, 6);

        Page<Coffee> coffeePage = coffeeService.searchByKeyword(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.onSuccess(CoffeeConverter.toCoffeeListDto(coffeePage)));
    }

    @GetMapping("/recommended/recent5")
    @Operation(
            summary = "최근 추천받은 음료 5개 API",
            description = """
             최근 추천받은 음료 5개를 보여주는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeePreviewDTO>>> get5RecentRecommendedCoffees() {
        String email = extractMemberEmail();
        Long id = memberService.getMemberIdByEmail(email);
        return ResponseEntity.ok(ApiResponse.onSuccess(coffeeService.get5RecentRecommendedCoffees(id)));
    }

    @GetMapping("recommended/all")
    @Operation(
            summary = "추천받은 음료 전부 보여주는 API",
            description = """
             사용자가 지금까지 추천받은 음료를 모두 최신순으로 보여주는 API입니다.
                """
    )
    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeePreviewDTO>>> getRecommendedCoffees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = extractMemberEmail();
        Long id = memberService.getMemberIdByEmail(email);
        return coffeeService.getRecommendedCoffees(id, page, size);
    }


}
