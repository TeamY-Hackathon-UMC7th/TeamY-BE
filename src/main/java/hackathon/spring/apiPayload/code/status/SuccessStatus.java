package hackathon.spring.apiPayload.code.status;

import hackathon.spring.apiPayload.code.BaseCode;
import hackathon.spring.apiPayload.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {

    // 일반적인 응답
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),

    // 멤버 응답
    _LOGIN_SUCCESS(HttpStatus.OK, "MEMBER200", "로그인에 성공했습니다."),
    _SIGNUP_SUCCESS(HttpStatus.OK, "MEMBER201", "회원가입에 성공했습니다."),
    _AVAILABLE_NICKNAME(HttpStatus.OK, "MEMBER200", "사용 가능한 닉네임입니다."),

    // 리뷰 관련 코드
    _REVIEW_SUCCESS(HttpStatus.OK, "REVIEW201", "리뷰 작성에 성공했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReason() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .build();
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}