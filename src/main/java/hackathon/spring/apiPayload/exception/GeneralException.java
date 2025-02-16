package hackathon.spring.apiPayload.exception;

import hackathon.spring.apiPayload.code.BaseErrorCode;
import hackathon.spring.apiPayload.code.ErrorReasonDTO;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private ErrorStatus errorStatus;
    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage()); // 기본 메시지는 ErrorStatus에서 가져옴
        this.errorStatus = errorStatus;
    }

    public class PasswordValidationException extends RuntimeException {
        public PasswordValidationException(String message) {
            super(message);
        }
    }
}