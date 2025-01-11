package hackathon.spring.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @Column(nullable = false, unique = true) // 닉네임은 UNIQUE
    private String nickname;
}
