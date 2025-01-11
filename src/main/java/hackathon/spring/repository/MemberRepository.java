package hackathon.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import hackathon.spring.domain.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByNickname(String nickname);
    Optional<Member> findByNickname(String nickname);
}
