package hackathon.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class) // Audit 기능 활성화
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recommendation {
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

    @CreatedDate // ✅ 엔티티가 생성될 때 자동으로 날짜 설정
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 추천받은 시간
}
