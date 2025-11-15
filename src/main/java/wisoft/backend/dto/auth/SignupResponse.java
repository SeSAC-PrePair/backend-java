package wisoft.backend.dto.auth;

public record SignupResponse(
        Long userId,
        String email,
        String name,
        String message) {

    public static SignupResponse of(
            Long userId,
            String email,
            String name) {
        return new SignupResponse(userId, email, name, "회원가입이 완료되었습니다.");
    }
}
