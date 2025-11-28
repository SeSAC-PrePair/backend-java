package wisoft.backend.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(

        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[!@#$%^&*])(.{6,})$",
                message = "비밀번호는 특수문자 1개 이상, 6자 이상이어야 합니다."
        )
        String password,

        @Valid
        SettingDto settings
) {
}
