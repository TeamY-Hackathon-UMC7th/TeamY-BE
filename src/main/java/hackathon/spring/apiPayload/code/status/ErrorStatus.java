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
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","존재하지 않는 회원입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "결과를 찾을 수 없는 요청입니다."),

    //로그인 관련 응답
    _INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "LOGIN4001", "유효하지 않은 토큰입니다."),
    _LOGOUT_TOKEN(HttpStatus.UNAUTHORIZED, "LOGIN4002", "로그아웃 처리된 토큰입니다."),

    //멤버 관련 응답
    _DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER409", "이미 사용 중인 닉네임입니다."),
    _DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER409", "이미 사용 중인 이메일입니다."),
    _EMPTY_EMAIL(HttpStatus.BAD_REQUEST, "MEMBER400", "이메일을 입력해야 합니다."),
    _INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER400", "올바른 이메일 형식이 아닙니다."),
    _EMPTY_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER400", "비밀번호를 입력해야 합니다."),
    _INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER400", "비밀번호는 영문, 숫자, 특수문자를 포함한 8~20자여야 합니다."),
    _NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER400", "비밀번호가 일치하지 않습니다."),
    _INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "MEMBER401", "비밀번호가 틀렸습니다."),
    _NOT_CHANGE_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER400", "이전 비밀번호와 동일합니다"),
    _EMPTY_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER400", "닉네임을 입력해야 합니다."),
    _INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER400", "닉네임은 10자 이내로 적어주세요."),
    _NOT_LOGIN_USER(HttpStatus.UNAUTHORIZED, "MEMBER401", "로그인을 먼저 진행해야 합니다."),
    _NOT_REGISTERED_USER(HttpStatus.UNAUTHORIZED, "MEMBER401", "회원가입을 먼저 진행해야 합니다."),

    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "사용자가 없습니다."),
    _LOGIN_FAILED(HttpStatus.UNAUTHORIZED,"MEMBER401", "비밀번호가 틀렸습니다."),

    //리뷰 관련 응답
    _NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTE404", "존재하지 않는 리뷰입니다."),
    _NOTE_NOT_EXIST(HttpStatus.NOT_FOUND, "NOTE404", "해당 사용자가 리뷰를 작성하지 않았습니다."),
    _NOTE_WRONG_INPUT(HttpStatus.BAD_REQUEST, "NOTE400", "입력된 노트의 형식이 잘못 되었습니다."),

    //시간 관련 응답
    _EMPTY_TIME_INPUT(HttpStatus.BAD_REQUEST, "TIME400", "시간을 입력해야 합니다."),
    _INVALID_TIME_FORMAT(HttpStatus.UNPROCESSABLE_ENTITY, "TIME422", "24시 형태로 입력해주세요."),


    //토큰 관련 응답
    _NEED_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "로그인이 되지 않았습니다."),

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
                .build();
    }
}
