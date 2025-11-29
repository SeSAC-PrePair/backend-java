package wisoft.backend.auth.dto;

public record SignupResponse(
        String userId,
        String email,
        String name,
        String message) {

    public static SignupResponse of(
            String userId,
            String email,
            String name) {
        return new SignupResponse(userId, email, name, "회원가입이 완료되었습니다.");
    }
}
