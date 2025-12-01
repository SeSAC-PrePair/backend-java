package wisoft.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외 (404 NOT FOUND)
 */
public class ResourceNotFoundException extends BaseException {
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ERROR_CODE, message);
    }

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(HttpStatus.NOT_FOUND, ERROR_CODE,
                String.format("%s을(를) 찾을 수 없습니다: %s", resourceType, identifier));
    }
}