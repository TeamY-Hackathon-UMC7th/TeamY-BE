package hackathon.spring.apiPayload.exception;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import lombok.Getter;

@Getter
public class PasswordValidationException extends RuntimeException {
    private final ErrorStatus errorStatus;

    public PasswordValidationException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}