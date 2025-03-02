package hackathon.spring.repository;

import hackathon.spring.domain.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    @EntityGraph(attributePaths = {"coffee"}) // 한 번의 쿼리로 Note + Coffee 조회
    List<Note> findByMemberId(Long memberId);
    Page<Note> findByMemberId(Long memberId, Pageable pageable);
    Integer countByMemberId(Long memberId);
}