package hackathon.spring.service;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.apiPayload.exception.Handler.NoteHandler;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Note;
import hackathon.spring.repository.CoffeeRepository;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.repository.NoteRepository;
import hackathon.spring.web.dto.NoteDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class NoteService {
    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;
    private final CoffeeRepository coffeeRepository;

//    public Long extractMemberIdFromToken(String token) {
//        if (token == null || !token.startsWith("Bearer ")) {
//            throw new RuntimeException("Token is missing or improperly formatted");
//        }
//        String jwtToken = token.replace("Bearer ", "");
//        try {
//            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512("${SECRET_KEY}"))
//                    .build()
//                    .verify(jwtToken);
//
//            return decodedJWT.getClaim("memberId").asLong();
//        } catch (JWTVerificationException e) {
//            throw new RuntimeException("Invalid or expired token");
//        }
//    }

    @Transactional
    public Note createNote(NoteDTO.NewNoteDTO dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_REGISTERED_USER));

        Coffee coffee = coffeeRepository.findById(dto.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        Note note = Note.builder()
                .member(member)
                .coffee(coffee)
                .drinkDate(LocalDateTime.parse(dto.getDrinkDate()))
                .sleepDate(LocalDateTime.parse(dto.getSleepDate()))
                .review(dto.getReview())
                .build();

        noteRepository.save(note);

        // ✅ NoteDTO 인스턴스를 생성하여 반환
        return note;
    }

    @Transactional
    public String deleteNote(Long memberId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._REVIEW_NOT_FOUND));

        if (!note.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED); // 권한 없음 예외
        }

        noteRepository.delete(note);
        return "Note is successfully deleted!";
    }

    @Transactional(readOnly = true)
    public NoteDTO.GetAllNotesDTO getAllNotes(Long memberId, int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Note> notesPage = noteRepository.findByMemberId(memberId, pageable);

        List<Note> notes = noteRepository.findByMemberId(memberId);
        if (notes.isEmpty()) {
            throw new NoteHandler(ErrorStatus._REVIEW_NOT_FOUND);
        }
        List<NoteDTO.NotePreviewDTO> notePreviews = notes.stream()
                .map(note -> new NoteDTO.NotePreviewDTO(
                        note.getId(),
                        new NoteDTO.CoffeePreviewDTO(
                                note.getCoffee().getBrand().name(),
                                note.getCoffee().getName(),
                                note.getCoffee().getCoffeeImgUrl()
                        ),
                        note.getWriteDate().toString(),
                        note.getDrinkDate().getHour(),
                        note.getSleepDate().getHour()
                ))
                .collect(Collectors.toList());

        // ✅ 페이지 정보 포함하여 DTO 생성
        return new NoteDTO.GetAllNotesDTO(
                page,
                notesPage.getTotalPages(),
                notePreviews
        );
    }

    @Transactional(readOnly = true)
    public NoteDTO.NoteDTO getNote(Long memberId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._REVIEW_NOT_FOUND));

        if (!note.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        return new NoteDTO.NoteDTO(
                new NoteDTO.CoffeePreviewDTO(
                        note.getCoffee().getBrand().name(),
                        note.getCoffee().getName(),
                        note.getCoffee().getCoffeeImgUrl()
                ),
                note.getWriteDate().toString(),
                note.getDrinkDate().toString(),
                note.getSleepDate().toString(),
                note.getReview()
        );
    }


}
