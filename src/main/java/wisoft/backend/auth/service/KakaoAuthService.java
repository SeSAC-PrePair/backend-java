package wisoft.backend.auth.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import wisoft.backend.auth.dto.response.KakaoTokenResponse;
import wisoft.backend.auth.entity.OAuthProvider;
import wisoft.backend.auth.entity.OAuthToken;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.OAuthTokenRepository;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.interviews.service.InterviewQueryService;


@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoAuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final OAuthTokenRepository oAuthTokenRepository;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    // 임시 토큰 저장소 (이메일 -> 토큰 정보)
    private final Map<String, TempKakaoToken> tempTokens = new ConcurrentHashMap<>();

    /**
     * 회원가입 전 카카오 토큰 임시 저장
     */
    @Transactional
    public void saveTempToken(String email, String authorizationCode) {
        try {

            KakaoTokenResponse tokenResponse = requestAccessToken(authorizationCode);
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn());

            tempTokens.put(email, new TempKakaoToken(
                    tokenResponse.getAccessToken(),
                    tokenResponse.getRefreshToken(),
                    expiresAt
            ));

            log.info("카카오 토큰 임시 저장 완료 - email: {}", email);
        } catch (Exception e) {
            log.error("카카오톡 토큰 요청 실패", email, e);
            throw new RuntimeException("카카오 인증에 실패했습니다. 다시 시도해주세요." + e);
        }
    }

    /**
     * 임시 저장된 토큰을 User에 연결
     */
    @Transactional
    public void linkTempTokenToUser(String email, User user) {
        TempKakaoToken tempToken = tempTokens.get(email);

        if (tempToken == null) {
            log.warn("임시 저장된 카카오 토큰이 없습니다 - email: {}", email);
            return;
        }

        try {
            OAuthToken oAuthToken = OAuthToken.builder()
                    .user(user)
                    .provider(OAuthProvider.KAKAO)
                    .accessToken(tempToken.getAccessToken())
                    .refreshToken(tempToken.getRefreshToken())
                    .tokenExpiresAt(tempToken.getExpiresAt())
                    .build();

            oAuthTokenRepository.save(oAuthToken);
            tempTokens.remove(email);

            log.info("임시 토큰을 User에 연결 완료 - userId: {}, email: {}", user.getId(), email);
        } catch (Exception e) {
            log.error("임시 토큰 DB 저장 실패" + user.getId(), email, e);
            throw new RuntimeException("토큰 저장에 실패했습니다.", e);
        }
    }

    /**
     * OAuth 콜백 처리: authorization code로 토큰 발급 및 저장
     */
    @Transactional
    public void handleCallback(String authorizationCode, String userId) {
        KakaoTokenResponse tokenResponse = requestAccessToken(authorizationCode);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn());

        OAuthToken oAuthToken =
                oAuthTokenRepository.findByUserIdAndProvider(userId, OAuthProvider.KAKAO)
                        .orElse(OAuthToken.builder()
                                .user(user)
                                .provider(OAuthProvider.KAKAO)
                                .accessToken(tokenResponse.getAccessToken())
                                .refreshToken(tokenResponse.getRefreshToken())
                                .tokenExpiresAt(expiresAt)
                                .build());

        oAuthToken.updateTokens(
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                expiresAt
        );

        oAuthTokenRepository.save(oAuthToken);
        log.info("카카오 토큰 저장 완료 - userId: {}", userId);
    }

    /**
     * Authorization code로 access_token과 refresh_token 발급
     */
    private KakaoTokenResponse requestAccessToken(String authorizationCode) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                KakaoTokenResponse.class
        );

        return response.getBody();
    }

    /**
     * refresh_token으로 access_token 갱신
     */
    @Transactional
    public String refreshAccessToken(String userId) {
        OAuthToken oAuthToken = oAuthTokenRepository.findByUserIdAndProvider(userId, OAuthProvider.KAKAO)
                .orElseThrow(() -> new RuntimeException("카카오 인증 정보가없습니다. 카카오 인증을 먼저 진행해주세요."));

        if (oAuthToken.getRefreshToken() == null) {
            throw new RuntimeException("Refresh token이없습니다. 카카오 인증을 먼저진행해주세요.");
        }

        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", clientId);
        params.add("refresh_token", oAuthToken.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoTokenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    KakaoTokenResponse.class
            );

            KakaoTokenResponse tokenResponse = response.getBody();
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn());

            // refresh_token은 갱신되지 않을 수 있으므로 기존 값 유지
            String newRefreshToken = tokenResponse.getRefreshToken() != null
                    ? tokenResponse.getRefreshToken()
                    : oAuthToken.getRefreshToken();

            oAuthToken.updateTokens(
                    tokenResponse.getAccessToken(),
                    newRefreshToken,
                    expiresAt
            );

            oAuthTokenRepository.save(oAuthToken);

            log.info("Access token 갱신 완료 - userId: {}", userId);

            return tokenResponse.getAccessToken();

        } catch (Exception e) {
            log.error("토큰 갱신 실패 - userId: {}", userId, e);
            throw new RuntimeException("토큰 갱신에 실패 했습니다. 다시 인증해주세요.");
        }
    }

    /**
     * 유효한 access_token 반환 (만료 시 자동 갱신)
     */
    @Transactional
    public String getValidAccessToken(String userId) {
        OAuthToken oAuthToken = oAuthTokenRepository.findByUserIdAndProvider(userId, OAuthProvider.KAKAO)
                .orElseThrow(() -> new RuntimeException("카카오 인증이 필요합니다."));

        if (oAuthToken.getAccessToken() == null) {
            throw new RuntimeException("카카오 인증이 필요합니다.");
        }

        // 토큰 만료 5분 전에 갱신
        if (oAuthToken.isExpiringSoon()) {
            log.info("토큰이 곧 만료됩니다. 갱신을 시작합니다 - userId: {}", userId);
            return refreshAccessToken(userId);
        }
        return oAuthToken.getAccessToken();
    }

    /**
     * 임시 카카오 토큰 저장용 내부 클래스
     */
    private static class TempKakaoToken {
        private final String accessToken;
        private final String refreshToken;
        private final LocalDateTime expiresAt;

        public TempKakaoToken(String accessToken, String refreshToken, LocalDateTime expiresAt) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresAt = expiresAt;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }
    }


    /**
     * 임시 토큰 삭제
     */
    public void removeTempToken(String email) {
        if (tempTokens.remove(email) != null) {
            log.info("임시 토큰 삭제 완료 - email: {}", email);
        }
    }
}
