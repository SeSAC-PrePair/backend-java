package wisoft.backend.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "oauth_token")
public class OAuthToken {

    @Id
    @GeneratedValue(strategy =
            GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "access_token", length = 500,
            nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "token_expires_at", nullable =
            false)
    private LocalDateTime tokenExpiresAt;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

    @Column(name = "created_at", nullable = false,
            updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 토큰 업데이트
     */
    public void updateTokens(String accessToken,
                             String refreshToken, LocalDateTime expiresAt) {
        this.accessToken = accessToken;
        if (refreshToken != null) {
            this.refreshToken = refreshToken;
        }
        this.tokenExpiresAt = expiresAt;
    }

    /**
     * 토큰 만료 여부 확인 (5분 버퍼)
     */
    public boolean isExpiringSoon() {
        return this.tokenExpiresAt.isBefore(LocalDateTime.now().plusMinutes(5));
    }

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return this.tokenExpiresAt.isBefore(LocalDateTime.now());
    }
}