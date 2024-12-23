package me.songha.concert.shared.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendSimpleEmail(String to, String subject, String contents) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(contents);
        message.setFrom(from);

        mailSender.send(message);
        log.info("Simple email sent to: {}", to);
    }

    public void sendEmail(String to, String subject, String body, List<String> attachmentFiles,
                          List<String> inlineFiles) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(from);

            addAttachments(attachmentFiles, helper);
            addInlineFiles(inlineFiles, helper);

            mailSender.send(mimeMessage);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("[Error] Cannot send email to: {}. Error message: {}", to, e.getMessage(), e);
        }
    }

    private void addAttachments(List<String> attachmentFiles, MimeMessageHelper helper) {
        if (attachmentFiles != null) {
            for (String pathAndName : attachmentFiles) {
                FileSystemResource file = new FileSystemResource(new File(pathAndName));
                String fileName = new File(pathAndName).getName();
                try {
                    helper.addAttachment(fileName, file);
                } catch (MessagingException e) {
                    log.error("[Error] Failed to add attachment: {}", fileName, e);
                }
            }
        }
    }

    private void addInlineFiles(List<String> inlineFiles, MimeMessageHelper helper) {
        if (inlineFiles != null) {
            for (String pathAndName : inlineFiles) {
                String cid = "image_" + new File(pathAndName).getName();
                FileSystemResource file = new FileSystemResource(new File(pathAndName));
                try {
                    helper.addInline(cid, file);
                } catch (MessagingException e) {
                    log.error("[Error] Failed to add inline image: {}", pathAndName, e);
                }
            }
        }
    }
}