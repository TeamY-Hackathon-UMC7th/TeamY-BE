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

    @Setter
    @Column(columnDefinition = "VARCHAR(10)")
    private String nickname;

    // 연관관계: Member -> Note (Cascade 적용)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    // 연관관계: Member -> Recommendation (Cascade 적용)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();


    public static Member createOAuthMember(String username, String email) {
        return Member.builder()
                .nickname(username)
                .email(email) // email 값을 claims에서 추출
                .build();
    }

}
