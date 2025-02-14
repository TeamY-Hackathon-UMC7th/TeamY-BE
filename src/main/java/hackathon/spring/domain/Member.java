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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long Id;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "VARCHAR(10)")
    private String nickname;

    @Column(nullable = false)
    private String password;

}
