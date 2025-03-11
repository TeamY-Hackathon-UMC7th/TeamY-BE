package hackathon.spring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.CoffeeServiceException;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.convertor.CoffeeConverter;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Note;
import hackathon.spring.domain.Recommendation;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.domain.uuid.Uuid;
import hackathon.spring.domain.uuid.UuidRepository;
import hackathon.spring.repository.CoffeeRepository;
//import hackathon.spring.s3.AmazonS3Manager;
import hackathon.spring.repository.RecommendationRepository;
import hackathon.spring.web.dto.CoffeeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
//    private final CoffeeRecommendRepository coffeeRecommendRepository;
//    private final AmazonS3Manager s3Manager;
//    private final UuidRepository uuidRepository;

    private final UuidRepository uuidRepository;
    private final CoffeeConverter coffeeConverter;
    private final RecommendationRepository recommendationRepository;

//    public Coffee addCoffee(String name, Brand brand,Integer sugar, Integer caffeine, Integer calories, Integer protein, MultipartFile coffeeImg) {
//        // UUID 생성 및 저장
//        String uuid = UUID.randomUUID().toString();
//        Uuid savedUuid = uuidRepository.save(Uuid.builder()
//                .uuid(uuid).build());
//
//        // 이미지 업로드
//        String imageKey = s3Manager.generateKeyName(savedUuid); // 커피 이미지에 적합한 KeyName 생성
//        String imageUrl = s3Manager.uploadFile(imageKey, coffeeImg);
//
//        // Coffee 객체 생성
//        Coffee newCoffee = Coffee.builder()
//                .name(name)
//                .brand(brand)
//                .sugar(sugar)
//                .caffeine(caffeine)
//                .calories(calories)
//                .protein(protein)
//                .coffeeImgUrl(imageUrl) // 이미지 URL 설정
//                .build();
//
//        // Coffee 객체 저장
//        return coffeeRepository.save(newCoffee);
//    }

    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeePreviewDTO>>> recommendByCaffeineLimit(Member member, Integer userHourInput) {
        if (userHourInput == null) {
            throw new GeneralException(ErrorStatus._EMPTY_TIME_INPUT);
        }

        try {
            if (userHourInput < 0 || userHourInput > 23) {
                throw new GeneralException(ErrorStatus._INVALID_TIME_FORMAT);
            }
        } catch (NumberFormatException e) {
            throw new GeneralException(ErrorStatus._INVALID_TIME_FORMAT);
        }

        int currentHour = LocalDateTime.now().getHour();

        long t;
        if (userHourInput <= currentHour) {
            t = ((userHourInput + 24) - currentHour) * 60; // 하루를 더해서 계산
        } else {
            t = (userHourInput - currentHour) * 60; // 같은 날 시간 계산
        }

        int minCaffeine = 0;
        int maxCaffeine = 0;

        if (t >= 480) {
            maxCaffeine = Integer.MAX_VALUE;
        } else if (t >= 390) {
            maxCaffeine = 150;
        } else if (t >= 300) {
            maxCaffeine = 120;
        } else if (t >= 240) {
            maxCaffeine = 100;
        } else {
            maxCaffeine = 0;
        }

        List<Coffee> coffeeList = coffeeRepository.findByCaffeineBetweenOrderByCaffeineAsc(minCaffeine, maxCaffeine);
        Collections.shuffle(coffeeList);  // 리스트를 무작위로 섞기

        // 상위 5개의 커피를 추천
        List<CoffeeDto.CoffeePreviewDTO> recommendedCoffees = coffeeList.stream()
                .limit(5)
                .map(CoffeeDto.CoffeePreviewDTO::fromEntity) // Coffee 엔티티에서 DTO 변환하는 메서드 필요
                .collect(Collectors.toList());

        //Recommendation에 넣기
        Coffee coffee = coffeeRepository.getOne(recommendedCoffees.get(0).getId());

        Recommendation recommendation = Recommendation.builder()
                .member(member)
                .coffee(coffee)
                .build();
        recommendationRepository.save(recommendation);

        //coffeedrinkcount에 넣기
        coffeeRepository.add1DrinkCount(coffee.getId());

        return ResponseEntity.ok(ApiResponse.onSuccess(recommendedCoffees));
    }

    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeeDetailPreviewDTO>>> getPopularCoffees(){
        // 커피 목록 조회
        List<Coffee> topDrinks = coffeeRepository.findTop5ByOrderByDrinkCountDesc();

        // 데이터가 없을 경우 예외 처리
        if (CollectionUtils.isEmpty(topDrinks)) {
            throw new GeneralException(ErrorStatus._NOT_FOUND);
        }

        // Coffee -> CoffeePreviewDTO 변환
        List<CoffeeDto.CoffeeDetailPreviewDTO> mostPopularCoffees5 = topDrinks.stream()
                .map(CoffeeDto.CoffeeDetailPreviewDTO::fromEntity) // Coffee 엔티티에서 DTO 변환하는 메서드 필요
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.onSuccess(mostPopularCoffees5));
    }


    public Page<Coffee> searchByKeyword(String keyword, Pageable pageable) {
        Page<Coffee> coffees = coffeeRepository.findByBrandOrNameContaining(keyword, pageable);

        if (coffees == null || coffees.isEmpty()) {
            throw new CoffeeServiceException(ErrorStatus._COFFEE_NOT_FOUND);
        }

//        List<CoffeeDto.CoffeeResponseDto> coffeeResponseDtos = coffees.stream()
//                .map(coffeeConverter::toCoffeeDto)
//                .collect(Collectors.toList());

        return coffees;
    }

    public List<CoffeeDto.CoffeePreviewDTO> get5RecentRecommendedCoffees(Long memberId) {
        // 커피 목록 조회
        List<Recommendation> recentRecommend5Coffees = recommendationRepository.findTop5ByMemberIdOrderByCreatedAtDesc(memberId);
        List<CoffeeDto.CoffeePreviewDTO> coffeePreviews = recentRecommend5Coffees.stream()
                .map(CoffeeConverter::toPreviewDTO)
                .collect(Collectors.toList());
        return coffeePreviews;
    }

    public ResponseEntity<ApiResponse<List<CoffeeDto.CoffeePreviewDTO>>> getRecommendedCoffees(Long memberId, int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        // 커피 목록 조회
        List<Recommendation> recentRecommendCoffees = recommendationRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        List<CoffeeDto.CoffeePreviewDTO> coffeePreviews = recentRecommendCoffees.stream()
                .map(CoffeeConverter::toPreviewDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.onSuccess(coffeePreviews));
    }


    public ResponseEntity<ApiResponse<CoffeeDto.CoffeeResponseDto>> getCoffeeInfo(Long coffeeId) {
        Coffee coffee = coffeeRepository.findCoffeeById(coffeeId);
        if (coffee == null) {
            throw new GeneralException(ErrorStatus._COFFEE_NOT_FOUND);
        }
        CoffeeDto.CoffeeResponseDto coffeeResponseDto = CoffeeConverter.toCoffeeDto(coffee);
        return ResponseEntity.ok(ApiResponse.onSuccess(coffeeResponseDto));
    }
}
