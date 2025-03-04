package hackathon.spring.service;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.apiPayload.exception.Handler.NoteHandler;
import hackathon.spring.convertor.CoffeeConverter;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Note;
import hackathon.spring.repository.CoffeeRepository;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.repository.NoteRepository;
import hackathon.spring.web.dto.CoffeeDto;
import hackathon.spring.web.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class NoteService {
    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;
    private final CoffeeRepository coffeeRepository;

    @Transactional
    public Note createNote(NoteDto.NewNoteDTO dto, Long memberId) {
        // 1️⃣ 입력값 검증 (비어있는 값 확인)
        if (dto.getDrinkDateTime() == null || dto.getSleepDateTime() == null) {
            throw new GeneralException(ErrorStatus._EMPTY_TIME_INPUT);
        }

        // 2️⃣ Member 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_REGISTERED_USER));

        // 3️⃣ Coffee 존재 여부 확인
        Coffee coffee = coffeeRepository.findById(dto.getCoffeeId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._COFFEE_NOT_FOUND));

        // ✅ Note 엔티티 생성 및 저장
        Note note = Note.builder()
                .member(member)
                .coffee(coffee)
                .drinkDateTime(dto.getDrinkDateTime())  // 그대로 사용
                .sleepDateTime(dto.getSleepDateTime())  // 그대로 사용
                .review(dto.getReview())
                .build();

        noteRepository.save(note);
        coffee.setDrinkCount(coffee.getDrinkCount() + 1);

        // ✅ NoteDTO 인스턴스를 생성하여 반환
        return note;
    }

    @Transactional
    public String deleteNote(Long memberId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOTE_NOT_FOUND));

        if (note.getMember().getId()!=memberId) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED); // 권한 없음 예외
        }

        noteRepository.delete(note);
        return "Note is successfully deleted!";
    }

    @Transactional(readOnly = true)
    public NoteDto.GetAllNotesDTO getAllNotes(Long memberId, int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<Note> notesPage = noteRepository.findByMemberId(memberId, pageable);

        List<Note> notes = noteRepository.findByMemberId(memberId);
        //노트 없으면 없는 거 보여주기
//        if (notes.isEmpty()) {
//            throw new NoteHandler(ErrorStatus._NOTE_NOT_FOUND);
//        }
        List<NoteDto.NotePreviewDTO> notePreviews = notes.stream()
                .map(note -> new NoteDto.NotePreviewDTO(
                        note.getId(),
                        CoffeeConverter.toPreviewDTO(note.getCoffee()),
                        note.getWriteDateTime().toLocalDate().toString(),
                        note.getDrinkDateTime().getHour(),
                        note.getSleepDateTime().getHour()
                ))
                .collect(Collectors.toList());


        // ✅ 페이지 정보 포함하여 DTO 생성
        return new NoteDto.GetAllNotesDTO(
                page,
                notesPage.getTotalPages(),
                notePreviews
        );
    }

    @Transactional(readOnly = true)
    public NoteDto.NoteDTO getNote(Long memberId, Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOTE_NOT_FOUND));

        if (note.getMember().getId()!=memberId) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }

        return new NoteDto.NoteDTO(
                CoffeeConverter.toPreviewDTO(note.getCoffee()),
                note.getWriteDateTime().toString(),
                note.getDrinkDateTime().toString(),
                note.getSleepDateTime().toString(),
                note.getReview()
        );
    }


}
