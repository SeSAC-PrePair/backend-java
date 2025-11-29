package wisoft.backend.interviews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wisoft.backend.interviews.dto.response.QuestionDetailResponse;
import wisoft.backend.interviews.dto.response.QuestionHistoryResponse;
import wisoft.backend.interviews.dto.response.TodayQuestionResponse;
import wisoft.backend.interviews.service.InterviewService;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/me/today")
    public ResponseEntity<TodayQuestionResponse> getTodayQuestion(
            @RequestHeader("X-User-ID") String userId
    ) {
        TodayQuestionResponse response = interviewService.getTodayQuestion(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/histories")
    public ResponseEntity<List<QuestionHistoryResponse>> getAllQuestion(
            @RequestHeader("X-User-ID") String userId
    ) {
        List<QuestionHistoryResponse> response = interviewService.getAllQuestion(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/histories/{historyId}")
    public ResponseEntity<QuestionDetailResponse> getQuestionDetail(
            @PathVariable String historyId,
            @RequestHeader("X-User-ID") String userId
    ) {
        QuestionDetailResponse response = interviewService.getQuestionDetail(historyId, userId);
        return ResponseEntity.ok(response);
    }
}
