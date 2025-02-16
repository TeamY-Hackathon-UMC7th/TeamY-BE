package hackathon.spring.apiPayload.exception;

import hackathon.spring.apiPayload.code.status.ErrorStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RestControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(RestControllerAdvice.class);

    /**
     * CoffeeServiceException 처리 메서드
     */
    @ExceptionHandler(CoffeeServiceException.class)
    public ResponseEntity<String> handleCoffeeServiceException(CoffeeServiceException ex) {
        // 예외 메시지 로깅
        log.error("CoffeeServiceException occurred: {}", ex.getMessage(), ex);

        // HTTP 500 상태 코드와 메시지 반환
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Coffee Service Error: " + ex.getMessage());
    }

    /**
     * IllegalArgumentException 처리 메서드
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // IllegalArgumentException 발생 시 처리
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
    }




}
