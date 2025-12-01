package wisoft.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * 비즈니스 로직 처리 중 발생하는 예외 (400 BAD_REQUEST)
 */
public class BusinessException extends BaseException {
    private static final String DEFAULT_ERROR_CODE = "BUSINESS_ERROR";

    public BusinessException(String message) {
        super(HttpStatus.BAD_REQUEST, DEFAULT_ERROR_CODE, message);
    }

    public BusinessException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, errorCode, message, cause);
    }
}