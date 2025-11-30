package wisoft.backend.auth.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wisoft.backend.auth.entity.ScheduleType;
import wisoft.backend.auth.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findBySchedule(ScheduleType scheduleType);
}
