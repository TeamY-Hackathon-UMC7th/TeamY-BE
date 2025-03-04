package hackathon.spring.repository;

import hackathon.spring.domain.Recommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    // 특정 사용자(memberId)의 전체 추천 내역 조회
    List<Recommendation> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    // 특정 사용자(memberId)의 최근 5개 추천 내역 조회 (생성일 내림차순 정렬)
    List<Recommendation> findTop5ByMemberIdOrderByCreatedAtDesc(Long memberId);
}
