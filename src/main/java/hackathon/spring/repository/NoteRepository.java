package hackathon.spring.repository;

import hackathon.spring.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByMemberId(Long memberId);
}