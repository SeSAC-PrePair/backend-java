package wisoft.backend.auth.dto.response;

public record LoginResponse(
        String userId,
        String email,
        String name,
        String message
) {

    public static LoginResponse of(String userId, String email, String name) {
        return new LoginResponse(userId, email, name, "로그인에 성공했습니다.");
    }
}
