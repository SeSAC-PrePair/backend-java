package wisoft.backend.question.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wisoft.backend.question.entity.InterviewQuestion;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {
    /**
     * 랜덤 N개
     */
    @Query(value = "SELECT * FROM interview_questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<InterviewQuestion> findRandomQuestions(@Param("limit") int limit);
}