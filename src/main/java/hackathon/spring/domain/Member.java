package hackathon.spring.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    // 비밀번호 해싱
    @Setter
    @Column(columnDefinition = "VARCHAR(10)")
    private String nickname;

    // 비밀번호 해싱
    @Setter
    @Column(nullable = false)
    private String password;

    // 연관관계: Member -> Note (Cascade 적용)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    // 연관관계: Member -> Recommendation (Cascade 적용)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();


}
