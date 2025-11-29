package wisoft.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DeleteUserRequest(
        @NotBlank
        @Pattern(
                regexp = "^(?=.*[!@#$%^&*])(.{6,})$",
                message = "비밀번호는 특수문자 1개 이상, 6자 이상이어야 합니다."
        )
        String password
) {
}
