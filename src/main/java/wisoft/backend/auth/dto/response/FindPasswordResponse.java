package wisoft.backend.auth.dto.response;

public record FindPasswordResponse(
        String email,
        String password,
        String message
) {
    public static FindPasswordResponse of(String email, String password) {
        return new FindPasswordResponse(email, password, "비밀번호를 찾았습니다.");
    }
}
