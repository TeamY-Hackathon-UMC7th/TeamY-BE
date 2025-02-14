package hackathon.spring.web.controller;

import hackathon.spring.apiPayload.ApiResponse;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import hackathon.spring.service.NoteService;
import hackathon.spring.web.dto.NoteResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/note")
public class NoteRestController {

    private final NoteService noteService;

    @GetMapping("")
    @Operation(summary = "전체 기록 조회 API", description = "사용자의 전체 기록 목록을 페이지네이션하여 조회합니다.")
    public ResponseEntity<ApiResponse<NoteResponseDTO.GetAllNotesDTO>> getAllNotes(
            @Parameter(description = "페이지 번호 (기본값: 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(defaultValue = "10") int size) {
        NoteResponseDTO.GetAllNotesDTO notes = noteService.getAllNotes(page, size);
        return ResponseEntity.ok(ApiResponse.onSuccess(notes, SuccessStatus._OK.getMessage()));
    }

    @GetMapping("/{noteId}")
    @Operation(summary = "단일 기록 조회 API", description = "지정된 noteId에 대한 기록 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<NoteResponseDTO.NoteDTO>> getNote(
            @Parameter(description = "조회할 기록의 ID") @PathVariable Long noteId) {
        NoteResponseDTO.NoteDTO note = noteService.getNote(noteId);
        return ResponseEntity.ok(ApiResponse.onSuccess(note, SuccessStatus._OK.getMessage()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    @Operation(summary = "기록 생성 API", description = "새로운 기록을 생성합니다. 인증이 필요합니다.")
    public ResponseEntity<ApiResponse<String>> createNote(
            @Parameter(description = "액세스 토큰") @RequestHeader("Authorization") String token,
            @RequestBody @Valid NoteResponseDTO.NewNoteDTO noteRequestDTO) {
//        String nickname = noteService.extractNicknameFromToken(token);
        return ResponseEntity.ok(noteService.createNote(noteRequestDTO, memberId));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{noteId}")
    @Operation(summary = "기록 삭제 API", description = "지정된 noteId의 기록을 삭제합니다. 인증이 필요합니다.")
    public ResponseEntity<ApiResponse<String>> deleteNote(
            @Parameter(description = "액세스 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "삭제할 기록의 ID") @PathVariable Long noteId) {
        String nickname = noteService.extractNicknameFromToken(token);
        return ResponseEntity.ok(noteService.deleteNote(noteId, nickname));
    }
}
