package hackathon.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import hackathon.spring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관관계 설정
    @JoinColumn(name = "member_id", nullable = false) // Foreign Key
    @JsonManagedReference
    private Member member; // 닉네임 (NOT NULL)

    @ManyToOne(fetch = FetchType.LAZY) // Coffee와 연관관계 설정
    @JoinColumn(name = "coffeeKey", nullable = false) // Foreign Key
    private Coffee coffee; // Coffee_Key (NOT NULL)

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime drinkTime; // 음용 시간

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sleepTime; // 수면 시간

    @Column(nullable = false)
    private String comment; // 코멘트
}
