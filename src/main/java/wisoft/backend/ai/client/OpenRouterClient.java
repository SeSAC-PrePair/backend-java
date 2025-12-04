package wisoft.backend.ai.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import wisoft.backend.ai.dto.AIPrompt;
import wisoft.backend.ai.dto.OpenRouterRequest;
import wisoft.backend.ai.dto.OpenRouterResponse;
import wisoft.backend.global.exception.ErrorCode;
import wisoft.backend.global.exception.custom.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenRouterClient {

    private final RestTemplate restTemplate;

    @Value("%{openrouter.api.key")
    private String apiKey;

    @Value("${openrouter.base.url")
    private String baseUrl;

    @Value("openrouter.model")
    private String model;

    public String generateQuestion(AIPrompt prompt) {
        try {
            // 프롬프트 생성
            String systemPrompt = buildSystemPrompt(prompt);

            // 요청 DTO 생성
            OpenRouterRequest request = OpenRouterRequest.of(model, prompt.temperature(), prompt.maxTokens(), systemPrompt);

            // OpenRouter API 호출
            OpenRouterResponse response = callOpenRouterAPI(request);

            // 응답에서 질문 추출
            return response.extractContent();

        } catch (Exception e) {
            log.error("AI 질문 생성 실패 - job: {}", prompt.job(), e);
            throw new BusinessException(ErrorCode.QUESTION_GENERATION_FAILED);
        }
    }

    /**
     * AI에 전달할 시스템 프롬프트 생성
     */
    private String buildSystemPrompt(AIPrompt prompt) {
        String samplesText = buildSampleQuestionText(prompt.sampleQuestions());
        String excludedText = buildExcludedQuestionText(prompt.excludedQuestions());

        return String.format(
                """
                        당신은 '%s' 면접관입니다.
                        
                        다음은 참고할 예시 질문들입니다:
                        %s
                        
                        중요: 다음은 이미 사용된 질문들이므로 절대 생성하지 마세요, 유사한 질문도 생성하지 마세요:
                        %s
                        
                        요구사항:
                        1. 예시 질문의 스타일과 난이도를 참고하되, 완전히 새로운 질문을 만들어주세요
                        2. '%s' 직무에 매우 적합한 실무 중심의 질문이어야 합니다
                        3. 질문은 구체적이고 상세해야 하며, 상황이나 맥락을 포함해주세요
                        4. 질문 길이는 최소 2문장 이상으로 작성하고, 배경 설명이나 구체적인 시나리오를 포함하세요
                        5. 단순한 'A에 대해 설명하세요' 보다는 '어떤 상황에서 A를 어떻게 활용했는지' 같이 구체적으로 물어보세요
                        6. 질문만 텍스트로 출력하고, 다른 설명이나 메타 정보는 포함하지 마세요
                        """,
                prompt.job(),
                samplesText,
                excludedText,
                prompt.job()
        );
    }

    /**
     * 샘플 질문 목록을 텍스트로 벼놘
     */
    private String buildSampleQuestionText(List<String> sampleQuestions) {
        if (sampleQuestions == null || sampleQuestions.isEmpty()) {
            return "(참고할 예시 질문 없음)";
        }

        return sampleQuestions.stream()
                .map(q -> "- " + q)
                .collect(Collectors.joining("\n"));
    }

    /**
     * 제외할 질문 목록을 텍스트로 변환
     */
    private String buildExcludedQuestionText(List<String> excludeQuestions) {
        if (excludeQuestions == null || excludeQuestions.isEmpty()) {
            return "(제외할 질문 없음)";

        }
        return excludeQuestions.stream()
                .map(q -> "- " + q)
                .collect(Collectors.joining("\n"));
    }

    /**
     * OpenRouter API 호출
     */
    private OpenRouterResponse callOpenRouterAPI(OpenRouterRequest request) {
        String url = baseUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "Interview Assistant");

        HttpEntity<OpenRouterRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<OpenRouterResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                OpenRouterResponse.class
        );

        return response.getBody();
    }
}
