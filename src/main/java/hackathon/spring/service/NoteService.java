package hackathon.spring.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.apiPayload.exception.GeneralException;
import hackathon.spring.apiPayload.exception.Handler.NoteHandler;
import hackathon.spring.domain.Coffee;
import hackathon.spring.domain.Member;
import hackathon.spring.domain.Note;
import hackathon.spring.repository.CoffeeRepository;
import hackathon.spring.repository.MemberRepository;
import hackathon.spring.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class NoteService {
    private final NoteRepository noteRepository;
    private final MemberRepository memberRepository;
    private final CoffeeRepository coffeeRepository;

    public String extractNicknameFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token is missing or improperly formatted");
        }

        String jwtToken = token.replace("Bearer ", "");
        try {
            return JWT.require(Algorithm.HMAC512("${SECRET_KEY}"))
                    .build()
                    .verify(jwtToken)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

    public String extractMemberIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("Token is missing or improperly formatted");
        }

        String jwtToken = token.replace("Bearer ", "");
        try {
            return JWT.require(Algorithm.HMAC512("${SECRET_KEY}"))
                    .build()
                    .verify(jwtToken)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> createNote(NoteResponseDTO dto, String nickname) {
        Optional<Member> memberOptional = memberRepository.findByNickname(nickname);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            Coffee coffee = coffeeRepository.findById(dto.getCoffeeKey())
                    .orElseThrow(() -> new RuntimeException("Coffee not found"));

            if (dto.getComment().length() > 200) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure("400", "Comment cannot exceed 200 characters.", null));
            }

            Note note = Note.builder()
                    .member(member)
                    .coffee(coffee)
                    .drinkTime(dto.getDrinkTime())
                    .sleepTime(dto.getSleepTime())
                    .comment(dto.getComment())
                    .build();

            noteRepository.save(note);
            return ResponseEntity
                    .status(SuccessStatus._REVIEW_SUCCESS.getHttpStatus())
                    .body(ApiResponse.onSuccess(
                            SuccessStatus._REVIEW_SUCCESS.getCode(),
                            SuccessStatus._REVIEW_SUCCESS.getMessage(),
                            "Note created successfully!"));
        } else {
            return ResponseEntity.status(ErrorStatus. _NOT_REGISTERED_USER.getHttpStatus())
                    .body(ApiResponse.onFailure(
                            ErrorStatus. _NOT_REGISTERED_USER.getCode(),
                            ErrorStatus. _NOT_REGISTERED_USER.getMessage(),
                            null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> deleteNote(Long noteId, String nickname) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._REVIEW_NOT_FOUND));

        if (!note.getMember().getNickname().equals(nickname)) {
            System.out.println("자신이 쓴 리뷰인지 확인하는 과정입니다. ");
            return ResponseEntity
                    .status(ErrorStatus._REVIEW_NOT_EXIST.getHttpStatus())
                    .body(ApiResponse.onSuccess(
                            ErrorStatus._REVIEW_NOT_EXIST.getCode(),
                            ErrorStatus._REVIEW_NOT_EXIST.getMessage(),
                            null));
        }

        noteRepository.delete(note);
        return ResponseEntity.ok(ApiResponse.onSuccess("Note deleted successfully!"));
    }

    @Transactional
    public List<Note> getAllNotes(Long memberId){
        //자신이 쓴 리뷰만 모두 가져와야 함
        List<Note> notes = noteRepository.findByMemberId(memberId);
        if(notes.isEmpty()) {
            throw new NoteHandler(ErrorStatus._REVIEW_NOT_FOUND);
        }
        return notes;
    }

}
