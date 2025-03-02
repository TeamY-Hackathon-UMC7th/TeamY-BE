package hackathon.spring.apiPayload.exception;

import hackathon.spring.apiPayload.code.ErrorReasonDTO;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public GeneralException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public ErrorReasonDTO getErrorReason() {
        return this.errorStatus.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.errorStatus.getReasonHttpStatus();
    }
}
