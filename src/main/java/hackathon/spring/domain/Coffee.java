package hackathon.spring.domain;

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
    private Brand brand; //

    @Column(nullable = false)
    private Integer sugar; //

    @Column(nullable = false)
    private Integer caffeine;

    private Integer calories;

    private Integer protein;

    private String coffeeImgUrl;

}
