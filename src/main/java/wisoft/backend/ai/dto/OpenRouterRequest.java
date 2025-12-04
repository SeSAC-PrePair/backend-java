package wisoft.backend.ai.dto;

import java.util.List;

public record OpenRouterRequest(
        String model,
        double temperature,
        int maxTokens,
        List<Message> messages
) {

    public record Message(
            String role,
            String content
    ) {}

    public static OpenRouterRequest of(
            String model,
            double temperature,
            int maxTokens,
            String systemPrompt
    ) {
        return new OpenRouterRequest(
                model,
                temperature,
                maxTokens,
                List.of(
                        new Message("system", systemPrompt),
                        new Message("user", "새로운 면접 질문을 생성해주세요. 질문만 출력하세요.")
                )
        );
    }
}
