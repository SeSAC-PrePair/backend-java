package wisoft.backend.interviews.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;
import wisoft.backend.interviews.entity.InterviewQuestion;
import wisoft.backend.interviews.repository.HistoryRepository;
import wisoft.backend.interviews.repository.InterviewQuestionRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionService {

    private static final int SAMPLE_QUESTION_COUNT = 10;
    private static final int MAX_TOKENS = 300;
    private static final double TEMPERATURE = 0.9;

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.base.url}")
    private String baseUrl;

    @Value("${openrouter.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final HistoryRepository historyRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 면접 질문 생성 및 저장
     */
    @Transactional
    public String generateQuestion(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

        // 이미 받은 질문들 조회
        List<String> askedQuestions = historyRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(History::getQuestion)
                .collect(Collectors.toList());

        // 예시 질문 랜덤 조회
        List<InterviewQuestion> sampleQuestions = interviewQuestionRepository
                .findRandomQuestions(SAMPLE_QUESTION_COUNT);

        // AI 질문 생성
        String newQuestion = generateQuestionWithSamples(
                user.getJob(),
                sampleQuestions,
                askedQuestions
        );

        // History에 저장
        History history = History.builder()
                .historyId("h_" + UUID.randomUUID())
                .user(user)
                .questionId("q_" + UUID.randomUUID())
                .question(newQuestion)
                .status(QuestionStatus.UNANSWERED)
                .build();

        historyRepository.save(history);
        log.info("질문 생성 완료 - userId: {}, question: {}", userId, newQuestion);

        return newQuestion;
    }

    /**
     * 스케줄러용 - 여러 사용자에게 질문 생성
     */
    @Transactional
    public void generateScheduledQuestions(List<String> userIds) {
        userIds.forEach(userId -> {
            try {
                generateQuestion(userId);
                log.info("스케줄 질문 생성 완료 - userId: {}", userId);
            } catch (Exception e) {
                log.error("스케줄 질문 생성 실패 - userId: {}", userId, e);
            }
        });
    }

    /**
     * 특정 사용자의 미답변 질문 목록 조회
     */
    public List<History> getUnansweredQuestions(String userId) {
        return historyRepository.findByUser_IdAndStatus(userId, QuestionStatus.UNANSWERED);
    }

    /**
     * AI를 활용한 맞춤 질문 생성 (중복 방지)
     */
    private String generateQuestionWithSamples(
            String jobPosition,
            List<InterviewQuestion> sampleQuestions,
            List<String> askedQuestions) {

        try {
            String systemPrompt = buildSystemPrompt(jobPosition, sampleQuestions, askedQuestions);
            Map<String, Object> requestBody = buildRequestBody(systemPrompt);

            ResponseEntity<Map> response = callOpenRouterAPI(requestBody);

            return extractQuestionFromResponse(response);

        } catch (Exception e) {
            log.error("면접 질문 생성 실패", e);
            throw new RuntimeException("면접 질문 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * AI에 전달한 시스템 프롬프트 생성 (입력은 JSON, 출력은 텍스트)
     */
    private String buildSystemPrompt(
            String jobPosition,
            List<InterviewQuestion> sampleQuestions,
            List<String> askedQuestions) {

        String samplesJson = buildSampleQuestionsJson(sampleQuestions);
        String askedQuestionsJson = buildAskedQuestionsJson(askedQuestions);

        String result = String.format(
                "당신은 '%s' 면접관입니다.\n\n" +
                        "다음은 참고할 예시 질문들입니다 (JSON 형식):\n" +
                        "%s\n\n" +
                        "중요: 다음은 이미 사용된 질문들이므로 절대 생성하지 마세요, 유사한 질문도 생성하지 마세요. (JSON 형식):\n" +
                        "%s\n\n" +
                        "요구사항:\n" +
                        "1. 예시 질문의 스타일과 난이도를 참고하되, 완전히 새로운 질문을 만들어주세요\n" +
                        "2. '%s' 직무에 매우 적합한 실무 중심의 질문이어야 합니다\n" +
                        "3. 질문은 구체적이고 상세해야 하며, 상황이나 맥락을 포함해주세요\n" +
                        "4. 질문 길이는 최소 2문장 이상으로 작성하고, 배경 설명이나 구체적인 시나리오를 포함하세요\n" +
                        "5. 단순한 'A에 대해 설명하세요' 보다는 '어떤 상황에서 A를 어떻게 활용했는지' 같이 구체적으로 물어보세요\n" +
                        "6. 질문만 텍스트로 출력하고, 다른 설명이나 메타 정보는 포함하지 마세요",
                jobPosition,
                samplesJson,
                askedQuestionsJson,
                jobPosition
        );

        return result;
    }

    /**
     * 예시 질문들을 JSON 문자열로 변환
     */
    private String buildSampleQuestionsJson(List<InterviewQuestion> sampleQuestions) {
        if (sampleQuestions.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < sampleQuestions.size(); i++) {
            InterviewQuestion q = sampleQuestions.get(i);
            json.append("  {\n");
            json.append(String.format("    \"question\": \"%s\",\n", escapeJson(q.getQuestion())));
            json.append(String.format("    \"difficulty\": \"%s\",\n", q.getDifficulty()));
            json.append(String.format("    \"category\": \"%s\"\n", q.getCategory()));
            json.append("  }");

            if (i < sampleQuestions.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    /**
     * 이미 물어본 질문들을 JSON 문자열로 변환
     */
    private String buildAskedQuestionsJson(List<String> askedQuestions) {
        if (askedQuestions.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[\n");

        for (int i = 0; i < askedQuestions.size(); i++) {
            json.append(String.format("  \"%s\"", escapeJson(askedQuestions.get(i))));

            if (i < askedQuestions.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("]");
        return json.toString();
    }

    /**
     * JSON 문자열 이스케이프 처리
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * OpenRouter API 요청 바디 생성
     */
    private Map<String, Object> buildRequestBody(String systemPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", TEMPERATURE);
        requestBody.put("max_tokens", MAX_TOKENS);

        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "새로운 면접 질문을 생성해주세요. 질문만 출력하세요.");
        messages.add(userMessage);

        requestBody.put("messages", messages);
        return requestBody;
    }

    /**
     * OpenRouter API 호출
     */
    private ResponseEntity<Map> callOpenRouterAPI(Map<String, Object> requestBody) {
        String url = baseUrl + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "Interview Assistant");

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
    }

    /**
     * API 응답에서 생성된 질문 추출 (단순 텍스트)
     */
    private String extractQuestionFromResponse(ResponseEntity<Map> response) {
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("choices")) {
            throw new RuntimeException("응답이 비어있습니다");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices.isEmpty()) {
            throw new RuntimeException("응답에 선택지가 없습니다");
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, String> message = (Map<String, String>) firstChoice.get("message");

        return message.get("content")
                .replace("\\\"", "\"")
                .replaceAll("^\"|\"$", "")
                .trim();

    }
}