package hackathon.spring.service;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.domain.uuid.Uuid;
import hackathon.spring.domain.uuid.UuidRepository;
import hackathon.spring.repository.CoffeeRepository;
import hackathon.spring.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class CoffeeService {
    private final CoffeeRepository coffeeRepository;
    private final AmazonS3Manager s3Manager;
    private final UuidRepository uuidRepository;

    public Coffee addCoffee(String name, Brand brand,Integer sugar, Integer caffeine, Integer calories, Integer protein, MultipartFile coffeeImg) {
        // UUID 생성 및 저장
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        // 이미지 업로드
        String imageKey = s3Manager.generateKeyName(savedUuid); // 커피 이미지에 적합한 KeyName 생성
        String imageUrl = s3Manager.uploadFile(imageKey, coffeeImg);

        // Coffee 객체 생성
        Coffee newCoffee = Coffee.builder()
                .name(name)
                .brand(brand)
                .sugar(sugar)
                .caffeine(caffeine)
                .calories(calories)
                .protein(protein)
                .coffeeImgUrl(imageUrl) // 이미지 URL 설정
                .build();

        // Coffee 객체 저장
        return coffeeRepository.save(newCoffee);
    }

    public List<Coffee> recommendByCaffeineLimit(LocalDateTime userTimeInput) {
        long t = ChronoUnit.MINUTES.between(LocalDateTime.now(), userTimeInput);

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
        return coffeeList.stream()
                .limit(5)         // 상위 5개만 반환
                .collect(Collectors.toList());
    }

    public List<Coffee> recommendPopularCoffees(){
        List<Coffee> allCoffees = coffeeRepository.findAll();
        if (allCoffees.isEmpty()) {
            throw new NoSuchElementException("커피 데이터가 존재하지 않습니다.");
        }
        Collections.shuffle(allCoffees);
        return allCoffees.stream()
                .limit(5)
                .collect(Collectors.toList());
    }


    public Page<Coffee> searchByKeyword(String keyword, Pageable pageable) {
        Page<Coffee> coffeeList = coffeeRepository.findByBrandOrNameContaining(keyword, pageable);

        if (coffeeList.isEmpty()) {
            throw new NoSuchElementException("검색하신 커피가 존재하지 않습니다.");
        } else {
            return coffeeList;
        }
    }

}
