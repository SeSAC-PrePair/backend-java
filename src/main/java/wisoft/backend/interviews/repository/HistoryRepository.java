package wisoft.backend.interviews.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import wisoft.backend.interviews.entity.History;

import java.time.LocalDateTime;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, String> {

    /**
     * 특정 사용자의 오늘 생성된 질문 조회
     */
    @Query(value = "SELECT * FROM history " +
            "WHERE user_id = :userId " +
            "AND created_at >= CURRENT_DATE " +
            "AND created_at < CURRENT_DATE + INTERVAL '1 day' " +
            "ORDER BY created_at DESC LIMIT 1",
            nativeQuery = true)
    Optional<History> findTodayQuestionByUserId(@Param("userId") String userId);


}
