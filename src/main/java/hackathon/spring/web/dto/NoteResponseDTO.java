package hackathon.spring.web.dto;

import hackathon.spring.domain.Coffee;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDTO {
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
        private CoffeePreviewDTO coffee;
        private String writeDate;
        private int drinkHour;
        private int sleepHour;
    }

    /**
     * 커피 정보를 표현하는 DTO
     * 사용 위치: NotePreviewDTO, NoteDTO 내부
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoffeePreviewDTO {
        private String coffeeBrand;
        private String coffeeName;
        private String coffeeImageURL;
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
        private CoffeePreviewDTO coffee;
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
        @NotNull(message = "coffeeId는 필수 입력값입니다.")
        private Long coffeeId;
        @NotNull(message = "drinkDate는 필수 입력값입니다.")
        private String drinkDate;
        @NotNull(message = "sleepDate는 필수 입력값입니다.")
        private String sleepDate;
        @Size(max = 200, message = "리뷰는 최대 200자까지 가능합니다.")
        private String review;
    }


}
