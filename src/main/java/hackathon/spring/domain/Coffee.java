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
    private Brand brand; // 브랜드 (NOT NULL)

    @Column(nullable = false)
    private Integer sugar; // 설탕 (NOT NULL)

    @Column(nullable = false)
    private Integer caffeine; // 카페인 (NOT NULL)

    private Integer calories; // 칼로리 (NULL 허용)

    private Integer protein; // 단백질 (NULL 허용)

    private String coffeeImgUrl; // 커피 이미지 URL (NULL 허용)

}
