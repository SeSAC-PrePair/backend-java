package wisoft.backend.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email
) {
}
