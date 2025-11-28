package wisoft.backend.auth.dto;

public record SignupResponse(
        Integer userId,
        String email,
        String name,
        String message) {

    public static SignupResponse of(
            Integer userId,
            String email,
            String name) {
        return new SignupResponse(userId, email, name, "회원가입이 완료되었습니다.");
    }
}
