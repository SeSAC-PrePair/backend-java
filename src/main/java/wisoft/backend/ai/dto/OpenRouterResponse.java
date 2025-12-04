package wisoft.backend.ai.dto;

import java.util.List;

public record OpenRouterResponse(
        String id,
        String model,
        List<Choice> choices
) {
    public record Choice(
            Message message,
            String finishReason // 종료 이유: "stop" (정상) | "length" (토큰 초과)
    ) {}

    public record Message(
            String role,
            String content
    ) {
    }

    public String extractContent() {
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("응답에 선택지가 없습니다.");
        }

        String content = choices.getFirst().message.content();

        return content.
                replace("\\\"", "\"")
                .replaceAll("^\"|\"$", "")
                .trim();
    }
}
