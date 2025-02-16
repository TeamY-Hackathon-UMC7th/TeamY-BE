package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.domain.Note;
import hackathon.spring.service.NoteService;
import hackathon.spring.web.dto.NoteDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteRestController {

    private final NoteService noteService;

    private Long extractMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 추출된 사용자 이메일 또는 ID
        return noteService.getMemberIdByEmail(email); // 이메일을 기반으로 memberId 가져오기
    }

    @GetMapping("")
    @Operation(summary = "전체 기록 조회 API", description = "사용자의 전체 기록 목록을 페이지네이션하여 조회합니다.")
    public ResponseEntity<ApiResponse<NoteDTO.GetAllNotesDTO>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long memberId = extractMemberId();
        return ResponseEntity.ok(ApiResponse.onSuccess(noteService.getAllNotes(memberId, page, size)));
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "단일 기록 조회 API", description = "지정된 noteId에 대한 기록 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<NoteDTO.NoteDto>> getNote(
            @PathVariable Long noteId) {
        Long memberId = extractMemberId();
        return ResponseEntity.ok(ApiResponse.onSuccess(noteService.getNote(memberId, noteId)));
    }

    @PostMapping("")
    @Operation(summary = "기록 생성 API", description = "새로운 기록을 생성합니다.")
    public ResponseEntity<ApiResponse<Note>> createNote(
            @RequestBody @Valid NoteDTO.NewNoteDTO noteRequestDTO) {
        Long memberId = extractMemberId();
        return ResponseEntity.ok(ApiResponse.onSuccess(noteService.createNote(noteRequestDTO, memberId)));
    }


    @DeleteMapping("/{noteId}")
    @Operation(summary = "기록 삭제 API", description = "지정된 noteId의 기록을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteNote(
            @PathVariable Long noteId) {
        Long memberId = extractMemberId();
        return ResponseEntity.ok(ApiResponse.onSuccess(noteService.deleteNote(memberId, noteId)));
    }




}
