package wisoft.backend.interviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wisoft.backend.interviews.entity.History;
import wisoft.backend.interviews.entity.QuestionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, String> {

    /**
     * 특정 사용자의 오늘 생성된 질문 조회 (최신순)
     */
    @Query(value = "SELECT * FROM history " +
            "WHERE user_id = :userId " +
            "AND created_at >= CURRENT_DATE " +
            "AND created_at < CURRENT_DATE + INTERVAL '1 day' " +
            "ORDER BY created_at DESC LIMIT 1",
            nativeQuery = true)
    Optional<History> findTodayQuestionByUserId(@Param("userId") String userId);

    /**
     * 특정 사용자의 모든 질문 내역 조회 (최신순)
     */
    List<History> findByUser_IdOrderByCreatedAtDesc(String userId);

    Optional<History> findByHistoryIdAndUser_Id(String historyId, String userId);

    List<History> findByUser_IdAndStatus(String userId, QuestionStatus questionStatus);
}
