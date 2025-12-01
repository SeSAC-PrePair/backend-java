package wisoft.backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("message", e.getMessage());

        // 메시지에 따라 적절한 HTTP 상태 코드 반환
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String message = e.getMessage();
        if (message != null) {
            if (message.contains("이미 사용 중인 이메일") || message.contains("이미 존재")) {
                status = HttpStatus.CONFLICT; // 409
            } else if (message.contains("존재하지 않는") || message.contains("찾을 수 없")) {
                status = HttpStatus.NOT_FOUND; // 404
            } else if (message.contains("비밀번호가 일치하지 않") || message.contains("인증")) {
                status = HttpStatus.UNAUTHORIZED; // 401
            }
        }

        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());

        return ResponseEntity.status(status).body(body);
    }
}
