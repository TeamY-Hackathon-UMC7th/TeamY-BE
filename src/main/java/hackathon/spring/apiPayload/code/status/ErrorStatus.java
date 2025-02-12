package hackathon.spring.apiPayload.code.status;

import hackathon.spring.apiPayload.code.BaseErrorCode;
import hackathon.spring.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    //가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","존재하지 않는 닉네임입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //멤버 관련 응답
    _DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER409", "이미 사용 중인 닉네임입니다."),
    _EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER400", "닉네임을 입력해야 합니다."),
    _NOT_LOGIN_USER(HttpStatus.BAD_REQUEST, "MEMBER401", "로그인을 먼저 진행해야 합니다."),
    _NOT_REGISTERED_USER(HttpStatus.BAD_REQUEST, "MEMBER402", "회원가입을 먼저 진행해야 합니다."),


    //리뷰 관련 응답
    _REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404", "존재하지 않는 리뷰입니다."),
    _REVIEW_NOT_EXIST(HttpStatus.NOT_FOUND, "REVIEW403", "해당 사용자가 리뷰를 작성하지 않았습니다."),

    //시간 관련 응답
    _EMPTY_TIME_INPUT(HttpStatus.BAD_REQUEST, "TIME400", "시간을 입력해야 합니다."),
    _INVALID_TIME_FORMAT(HttpStatus.BAD_REQUEST, "TIME422", "24시 형태로 입력해주세요."),


    //토큰 관련 응답
    _NEED_TOKEN(HttpStatus.BAD_REQUEST, "TOKEN401", "로그인이 되지 않았습니다."),

    //커피 관련 응답
    _COFFEE_NOT_FOUND(HttpStatus.NOT_FOUND, "COFFEE404", "존재하지 않는 커피입니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
