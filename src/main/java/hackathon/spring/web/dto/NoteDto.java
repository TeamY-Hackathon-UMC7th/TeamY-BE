package hackathon.spring.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;


public class NoteDto {
    /**
     * 전체 기록 조회 API의 응답 DTO
     * 사용 위치: GET /note
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GetAllNotesDTO {
        private int currentPage;
        private int totalPage;
        private List<NotePreviewDTO> content;
    }

    /**
     * 기록 리스트에서 각 개별 기록을 표현하는 DTO
     * 사용 위치: GET /note (List 내부)
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotePreviewDTO {
        private Long noteId;
        private CoffeeDto.CoffeePreviewDTO coffee;
        private String writeDate;
        private int drinkHour;
        private int sleepHour;
    }


    /**
     * 단일 기록 조회 API의 응답 DTO
     * 사용 위치: GET /note/{noteId}
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoteDTO {
        private CoffeeDto.CoffeePreviewDTO coffee;
        private String writeDate;
        private String drinkDate;
        private String sleepDate;
        @Size(max = 200, message = "리뷰는 최대 200자까지 가능합니다.")
        private String review;
    }

    /**
     * 기록 생성 API의 요청 DTO
     * 사용 위치: POST /note
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NewNoteDTO {
        @Schema(example = "2025-02-17 13:30", description = "마신 날짜와 시간 (YYYY-MM-DD HH:mm) 형식")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime drinkDateTime;

        @Schema(example = "2025-02-17 23:45", description = "수면 날짜와 시간 (YYYY-MM-DD HH:mm) 형식")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime sleepDateTime;

        private String review;
        private Long coffeeId;
    }


}
