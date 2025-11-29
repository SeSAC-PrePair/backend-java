package wisoft.backend.auth.dto.response;

public record EmailResponse(
        String message
) {
    public static EmailResponse of(String message) {
        return new EmailResponse(message);
    }
}
