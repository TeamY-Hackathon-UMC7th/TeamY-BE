package hackathon.spring.apiPayload.exception.Handler;

import hackathon.spring.apiPayload.code.BaseErrorCode;
import hackathon.spring.apiPayload.exception.GeneralException;

public class NoteHandler extends GeneralException {
    public NoteHandler(BaseErrorCode code) {
        super(code);
    }
}
