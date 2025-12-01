package wisoft.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * 리소스 충돌 시 발생하는 예외 (409 CONFLICT)
 */
public class ConflictException extends BaseException {
    private static final String ERROR_CODE = "RESOURCE_CONFLICT";

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, ERROR_CODE, message);
    }
}