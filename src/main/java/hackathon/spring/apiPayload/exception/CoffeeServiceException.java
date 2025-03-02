package hackathon.spring.apiPayload.exception;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import lombok.Getter;

/**
 * CoffeeServiceException: CoffeeService와 관련된 모든 예외 처리 클래스
 */
@Getter
public class CoffeeServiceException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public CoffeeServiceException(ErrorStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }

    public CoffeeServiceException(ErrorStatus errorStatus, Throwable cause) {
        super(errorStatus.getMessage(), cause);
        this.errorStatus = errorStatus;
    }
}