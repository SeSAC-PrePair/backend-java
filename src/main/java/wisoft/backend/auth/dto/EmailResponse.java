package wisoft.backend.auth.dto;

public record EmailResponse(
        String message
) {
    public static EmailResponse of(String message) {
        return new EmailResponse(message);
    }
}
