package hackathon.spring.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import hackathon.spring.apiPayload.code.BaseCode;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess","code","message","result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code; //API의 상태 코드(응답 코드)
    private final String message; //API 응답에 대한 설명 메시지
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> onSuccess(BaseCode code, T result){
            return new ApiResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), result);
    }

    // 성공한 경우 응답 생성 (동적 메시지 추가)
    public static <T> ApiResponse<T> onSuccess(T result, String customMessage) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), customMessage != null ? customMessage : SuccessStatus._OK.getMessage(), result);
    }


    // 실패한 경우 응답 생성 (ErrorStatus 활용)
    public static <T> ApiResponse<T> onFailure(ErrorStatus errorStatus, T result) {
        return new ApiResponse<>(
                false,
                errorStatus.getCode(),  // 에러 코드
                errorStatus.getMessage(), // 에러 메시지
                result);
    }

    public static <T> ApiResponse<T> onFailure(String code, T data) {
        return new ApiResponse<>(false, code, "요청 처리 중 오류가 발생했습니다.", data);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    public static ResponseEntity<ApiResponse> onSuccess(SuccessStatus status, Object result) {
        return ResponseEntity.ok(
                new ApiResponse(true, status.getCode(), status.getMessage(), result));
    }


    public static <T> ApiResponse<T> onSuccess(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }
}
