package hackathon.spring.apiPayload.exception.Handler;

import hackathon.spring.apiPayload.code.BaseErrorCode;
import hackathon.spring.apiPayload.code.status.ErrorStatus;
import hackathon.spring.apiPayload.exception.GeneralException;

public class ReviewHandler extends GeneralException {
    public ReviewHandler(ErrorStatus code) {
        super(code);
    }
}
