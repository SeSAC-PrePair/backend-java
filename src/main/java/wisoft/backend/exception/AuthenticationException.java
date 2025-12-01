package wisoft.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * 인증 실패 시 발생하는 예외 (401 UNAUTHORIZED)
 */
public class AuthenticationException extends BaseException {
    private static final String ERROR_CODE = "AUTHENTICATION_FAILED";

    public AuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, ERROR_CODE, message);
    }

    public AuthenticationException(String errorCode, String message) {
        super(HttpStatus.UNAUTHORIZED, errorCode, message);
    }
}