package wisoft.backend.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("🤖 [PrePair] 이메일 인증 코드");
            helper.setText(createEmailContent(code), true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 발송 실패", e);
        }
    }

    private String createEmailContent(String code) {
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f8f9ff;">
                    <div style="max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);">
                
                        <!-- Header -->
                        <div style="background: linear-gradient(135deg, #6B7FFF 0%%, #8B9FFF 100%%); padding: 50px 20px; text-align: center;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700; letter-spacing: -0.5px;">
                                PrePair
                            </h1>
                            <p style="margin: 8px 0 0 0; color: rgba(255, 255, 255, 0.9); font-size: 14px; font-weight: 400;">
                                완벽한 면접 준비를 위한 AI 파트너
                            </p>
                        </div>
                
                        <!-- Content -->
                        <div style="padding: 50px 40px;">
                            <h2 style="margin: 0 0 16px 0; color: #1a1a1a; font-size: 22px; font-weight: 700; text-align: center;">
                                이메일 인증
                            </h2>
                
                            <p style="margin: 0 0 40px 0; color: #666666; font-size: 15px; line-height: 1.6; text-align: center;">
                                회원가입을 완료하기 위해 아래 인증 코드를 입력해주세요.
                            </p>
                
                            <!-- Verification Code Box -->
                            <div style="background: linear-gradient(135deg, #f8f9ff 0%%, #f0f2ff 100%%); border: 2px solid #6B7FFF; border-radius: 16px; padding: 40px 20px; text-align: center; margin: 0 0 40px 0;">
                                <div style="color: #999999; font-size: 12px; margin-bottom: 12px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px;">
                                    VERIFICATION CODE
                                </div>
                                <div style="font-size: 42px; font-weight: 800; color: #6B7FFF; letter-spacing: 10px; font-family: 'Courier New', monospace;">
                                    %s
                                </div>
                            </div>
                
                            <div style="background-color: #f8f9ff; border-radius: 12px; padding: 20px; margin: 0 0 30px 0;">
                                <p style="margin: 0; color: #666666; font-size: 13px; line-height: 1.7;">
                                    • 본인이 요청하지 않은 경우 이 메일을 무시하세요.<br>
                                    • 인증 코드는 타인에게 공유하지 마세요.<br>
                                    • 인증번호의 유효 시간은 5분입니다.
                                </p>
                            </div>
                
                            <div style="text-align: center; padding-top: 20px; border-top: 1px solid #e8eaff;">
                                <p style="margin: 0; color: #999999; font-size: 13px;">
                                    PrePair 팀
                                </p>
                            </div>
                        </div>
                
                        <!-- Footer -->
                        <div style="background-color: #f8f9ff; padding: 24px; text-align: center;">
                            <p style="margin: 0; color: #999999; font-size: 12px; line-height: 1.6;">
                                AI 기반 맞춤형 면접 질문 서비스
                            </p>
                            <p style="margin: 8px 0 0 0; color: #cccccc; font-size: 11px;">
                                © 2025 PrePair. All rights reserved.
                            </p>
                        </div>
                
                    </div>
                </body>
                </html>
                """, code);
    }
}
