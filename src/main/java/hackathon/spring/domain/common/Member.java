package hackathon.spring.domain.common;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
    @Id
    @Column(unique = true, nullable = false)
    private String nickname;
}
