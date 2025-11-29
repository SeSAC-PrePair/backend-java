package wisoft.backend.auth.dto.response;

public record DeleteUserResponse(
        String message
) {
    public static DeleteUserResponse of(String name) {
        return new DeleteUserResponse(name + "님의 회원탈퇴가 완료되었습니다.");
    }
}
