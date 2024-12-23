package me.songha.concert.shared.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.songha.concert.shared.util.RandomNumberGenerator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@RequiredArgsConstructor
@Slf4j
@Service
public class CertEmailService {
    private final EmailService emailService;
    private final SpringTemplateEngine templateEngine;
    private static final String HTML_TYPE = "cert/index";

    public String generateAndSendCertificationCode(String to) {
        String code = generateCertificationCode();
        emailService.sendEmail(to, "[서비스] 인증번호 발송 안내", setContext(code), null, null);
        log.info("Email sent to: {}, code: {}", to, code);
        return code;
    }

    private String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(HTML_TYPE, context);
    }

    private String generateCertificationCode() {
        return RandomNumberGenerator.generateCertificationCode(6);
    }

}
