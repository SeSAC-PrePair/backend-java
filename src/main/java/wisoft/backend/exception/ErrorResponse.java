package wisoft.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 통일된 에러 응답 형식
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String errorCode;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String errorCode, String message) {
        this();
        this.status = status;
        this.error = error;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(int status, String error, String errorCode, String message, String path) {
        this(status, error, errorCode, message);
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        this(status, error, "VALIDATION_FAILED", message, path);
        this.fieldErrors = fieldErrors;
    }

    /**
     * 필드 검증 에러 상세 정보
     */
    @Getter
    @Setter
    public static class FieldError {
        private String field;
        private String rejectedValue;
        private String message;

        public FieldError() {
        }

        public FieldError(String field, String rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }
    }
}
