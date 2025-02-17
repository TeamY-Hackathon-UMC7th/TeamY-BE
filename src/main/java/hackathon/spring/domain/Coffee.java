package hackathon.spring.domain;

import hackathon.spring.domain.common.BaseEntity;
import hackathon.spring.domain.enums.Brand;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 이름 (NOT NULL)

    @Enumerated(EnumType.STRING) // 브랜드는 Enum 타입
    @Column(nullable = false)
    private Brand brand;

    @Column(nullable = true)
    private Integer sugar;

    @Column(nullable = false)
    private Integer caffeine;

    @Column(nullable = true)
    private Integer calories;

    @Column(nullable = true)
    private Integer protein;

    @Column(nullable = false)
    private String coffeeImgUrl; // 이미지 URL도 NOT NULL

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer drinkCount; // 사람들이 많이 마신 횟수 (초기값 0)

}
