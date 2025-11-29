package wisoft.backend.question.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wisoft.backend.question.dto.QuestionResponse;
import wisoft.backend.question.service.QuestionService;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

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
