package wisoft.backend.interviews.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wisoft.backend.interviews.entity.InterviewQuestion;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    /**
     * 랜덤으로 N개의 면접 질문 예시를 조회
     * AI 질문 생성 스타일과 난이도 참고용으로 사용
     */
    @Query(value = "SELECT * FROM interview_questions " +
            "ORDER BY RANDOM() LIMIT :limit",
            nativeQuery = true)
    List<InterviewQuestion> findRandomQuestions(@Param("limit") int limit);
}