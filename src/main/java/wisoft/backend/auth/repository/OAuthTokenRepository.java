package wisoft.backend.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wisoft.backend.auth.entity.OAuthProvider;
import wisoft.backend.auth.entity.OAuthToken;

@Repository
  public interface OAuthTokenRepository extends JpaRepository<OAuthToken, Long> {

      /**
       * 특정 사용자의 특정 제공자 토큰 조회
       */
      Optional<OAuthToken> findByUserIdAndProvider(String userId, OAuthProvider provider);

  }
