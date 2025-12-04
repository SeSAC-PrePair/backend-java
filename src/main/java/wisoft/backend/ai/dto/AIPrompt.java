package wisoft.backend.ai.dto;

import java.util.List;

public record AIPrompt(
        String job,
        List<String> sampleQuestions,
        List<String> excludedQuestions,
        int maxTokens,
        double temperature
) {
    public static AIPrompt of(
            String job,
            List<String> sampleQuestions,
            List<String> excludedQuestions
    ) {
        return new AIPrompt(
                job,
                sampleQuestions,
                excludedQuestions,
                300,
                0.9
        );
    }
}
