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
    @Column(nullable = false, unique = true,columnDefinition = "VARCHAR(20)")
    private String nickname;

    @Column(nullable = false)
    private String password;
}
