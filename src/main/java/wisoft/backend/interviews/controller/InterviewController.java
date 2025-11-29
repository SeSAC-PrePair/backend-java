package wisoft.backend.interviews.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.backend.interviews.dto.response.TodayQuestionResponse;
import wisoft.backend.interviews.service.InterviewService;

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
}
