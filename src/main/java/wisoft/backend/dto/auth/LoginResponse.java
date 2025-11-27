package wisoft.backend.dto.auth;

public record LoginResponse(
        Long userId,
        String email,
        String name,
        String message
) {

    public static LoginResponse of(Long userId, String email, String name) {
        return new LoginResponse(userId, email, name, "로그인에 성공했습니다.");
    }
}
