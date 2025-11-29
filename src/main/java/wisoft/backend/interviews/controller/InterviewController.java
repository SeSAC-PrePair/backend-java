package wisoft.backend.interviews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wisoft.backend.interviews.dto.response.QuestionDetailResponse;
import wisoft.backend.interviews.dto.response.QuestionHistoryResponse;
import wisoft.backend.interviews.dto.response.TodayQuestionResponse;
import wisoft.backend.interviews.service.InterviewQueryService;
import wisoft.backend.interviews.dto.response.QuestionResponse;
import wisoft.backend.interviews.service.QuestionService;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewQueryService interviewQueryService;
    private final QuestionService questionService;

    @GetMapping("/me/today")
    public ResponseEntity<TodayQuestionResponse> getTodayQuestion(
            @RequestHeader("X-User-ID") String userId
    ) {
        TodayQuestionResponse response = interviewQueryService.getTodayQuestion(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/histories")
    public ResponseEntity<List<QuestionHistoryResponse>> getAllQuestion(
            @RequestHeader("X-User-ID") String userId
    ) {
        List<QuestionHistoryResponse> response = interviewQueryService.getAllQuestion(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/histories/{historyId}")
    public ResponseEntity<QuestionDetailResponse> getQuestionDetail(
            @PathVariable String historyId,
            @RequestHeader("X-User-ID") String userId
    ) {
        QuestionDetailResponse response = interviewQueryService.getQuestionDetail(historyId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/first")
    public ResponseEntity<QuestionResponse> generateFirstQuestion(
            @RequestHeader("X-User-ID") String userId) {

        String question = questionService.generateQuestion(userId);

        return ResponseEntity.ok(QuestionResponse.builder()
                .question(question)
                .message("첫 면접 질문이 생성되었습니다!")
                .build());
    }
}
