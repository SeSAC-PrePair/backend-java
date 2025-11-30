package wisoft.backend.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import wisoft.backend.auth.entity.NotificationType;
import wisoft.backend.auth.entity.User;
import wisoft.backend.auth.repository.UserRepository;
import wisoft.backend.auth.service.KakaoAuthService;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final RestTemplate restTemplate;
    private final KakaoAuthService kakaoAuthService;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 질문 생성 시 알림 전송 (카카오톡 또는 이메일)
     */
    public void sendQuestionNotification(String userId, String question) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        try {
            if (user.getNotificationType() == NotificationType.BOTH
                    || user.getNotificationType() == NotificationType.KAKAO) {
                sendKakaoMessage(userId, question);
                sendEmailNotification(user, question);
            } else if (user.getNotificationType() == NotificationType.EMAIL) {
                sendEmailNotification(user, question);
            }
        } catch (Exception e) {
            log.error("알림 전송 실패 - userId: {}, type: {}", userId, user.getNotificationType(), e);
        }
    }

    /**
     * 카카오톡 "나에게 보내기" 메시지 전송
     */
    private void sendKakaoMessage(String userId, String question) {
        try {
            String accessToken = kakaoAuthService.getValidAccessToken(userId);

            String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + accessToken);

            // 카카오톡 템플릿 메시지 구성
            String templateObject = buildKakaoTemplate(question);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("template_object", templateObject);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("카카오톡 전송 성공 - userId: {}", userId);

        } catch (Exception e) {
            log.error("카카오톡 전송 실패 - userId: {}", userId, e);
            throw new RuntimeException("카카오톡 전송에  실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 카카오톡 템플릿 JSON 생성
     */
    private String buildKakaoTemplate(String question) {
        String answerUrl = frontendUrl + "/coach";

        return String.format("""
                        {
                            "object_type": "text",
                            "text": "📬 새로운 면접 질문이 도착했어요!\\n\\n %s\\n\\n지금 바로 답변하고 성장하세요! 💪",
                            "link": {
                                "web_url": "%s",
                                "mobile_web_url": "%s"
                            },
                            "button_title": "답변 작성하기"
                        }
                        """,
                escapeJson(question),
                answerUrl,
                answerUrl
        );
    }

    /**
     * 이메일 전송
     */
    private void sendEmailNotification(User user, String question) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("📬 새로운 면접 질문이 도착했습니다!");

            // HTML로 질문 포맷팅 (줄바꿈 처리)
            String formattedQuestion = question.replace("\n", "<br>");

            String emailBody = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                            </head>
                            <body style="margin: 0; padding: 20px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif;">
                                <div style="max-width: 600px; margin: 0 auto; background: white;">
                            
                                    <h2 style="margin: 0 0 20px 0; color: #333;">안녕하세요, %s님! 🙌</h2>
                            
                                    <p style="margin: 0 0 20px 0; color: #666; line-height: 1.6;">
                                        오늘의 <strong>새로운 면접 질문</strong>이 도착했습니다.
                                    </p>
                            
                                    <!-- 질문 박스 -->
                                    <div style="background: #f8f9fa; border-left: 4px solid #4A8CFF; border-radius: 8px; padding: 20px; margin-bottom: 25px;">
                                        <div style="color: #4A8CFF; font-weight: bold; margin-bottom: 12px; font-size: 16px;">
                                            🎯 오늘의 질문
                                        </div>
                                        <div style="color: #333; line-height: 1.8;">
                                            %s
                                        </div>
                                    </div>
                            
                                    <!-- 답변 버튼 -->
                                    <table cellpadding="0" cellspacing="0" border="0" style="margin-bottom: 30px;">
                                        <tr>
                                            <td style="border-radius: 8px; background: #4A8CFF;">
                                                <a href="%s/coach" 
                                                   style="display: inline-block; padding: 14px 28px; color: #ffffff; 
                                                          text-decoration: none; font-weight: bold; font-size: 15px;">
                                                    ✏️ 답변 작성하기
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
                            
                                    <!-- 전체 질문 링크 -->
                                    <p style="margin: 0 0 30px 0; color: #666;">
                                        👉 <a href="%s/interview/questions" style="color: #4A8CFF; text-decoration: none;">전체 질문 보기</a>
                                    </p>
                            
                                    <!-- 구분선 -->
                                    <div style="border-top: 1px solid #e9ecef; margin: 30px 0;"></div>
                            
                                    <!-- 푸터 -->
                                    <p style="margin: 0; color: #999; font-size: 14px; line-height: 1.6;">
                                        언제나 %s님의 성장을 응원합니다 🌱<br>
                                        PrePair 팀 드림 🤍
                                    </p>
                            
                                </div>
                            </body>
                            </html>
                            """,
                    user.getName(),
                    formattedQuestion,
                    frontendUrl,
                    frontendUrl,
                    user.getName()
            );

            helper.setText(emailBody, true);
            mailSender.send(message);

            log.info("이메일 전송 성공 - userId: {}, email: {}", user.getId(), user.getEmail());

        } catch (Exception e) {
            log.error("이메일 전송 실패 - userId: {}", user.getId(), e);
            throw new RuntimeException("이메일 전송에 실 패했습니다: " + e.getMessage());
        }
    }

    /**
     * JSON 문자열 이스케이프
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
