package hackathon.spring.service;

import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.enums.Brand;
import hackathon.spring.domain.uuid.Uuid;
import hackathon.spring.domain.uuid.UuidRepository;
import hackathon.spring.repository.CoffeeRepository;
import hackathon.spring.s3.AmazonS3Manager;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

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




}
