package hackathon.spring.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    public void setPassword(String password) {
        this.password = password;  // 비밀번호 해싱
    }
}
