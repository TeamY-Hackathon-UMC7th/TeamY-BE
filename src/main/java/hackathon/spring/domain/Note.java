package hackathon.spring.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import hackathon.spring.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Member와 연관관계 설정 - 지연 로딩
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore // 순환 참조 방지
    private Member member; // 사용자 정보 (NOT NULL)

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 변경
    @JoinColumn(name = "coffee_id", nullable = false)
    @JsonIgnore // 순환 참조 방지
    private Coffee coffee; // 커피 정보

    @CreatedDate // 엔티티가 생성될 때 자동으로 날짜 설정
    @Column(nullable = false, updatable = false)
    private LocalDateTime writeDateTime;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime drinkDateTime; // 마신 날짜 및 시간

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime sleepDateTime; // 수면 날짜 및 시간

    @Column(length = 200, nullable = true)
    private String review; // 리뷰 안 써도 괜찮음
}
