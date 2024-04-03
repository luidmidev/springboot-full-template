package com.luidmidev.template.spring.services.emails;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendSimpleMail(String to, String subject, String text) {

        var emailDetails = new EmailDetails(to, subject, text, null);
        sendSimpleMail(emailDetails);

    }

    public void sendMailWithAttachment(String to, String subject, String text, String attachment) throws MessagingException {
        var emailDetails = new EmailDetails(to, subject, text, attachment);
        sendMailWithAttachment(emailDetails);
    }

    public void sendSimpleMail(EmailDetails details) {

        var mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(sender);
        mailMessage.setTo(details.getTo());
        mailMessage.setText(details.getText());
        mailMessage.setSubject(details.getSubject());

        javaMailSender.send(mailMessage);

    }

    public void sendMailWithAttachment(EmailDetails details) throws MessagingException {

        var mimeMessage = javaMailSender.createMimeMessage();
        var mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom(sender);
        mimeMessageHelper.setTo(details.getTo());
        mimeMessageHelper.setText(details.getText());
        mimeMessageHelper.setSubject(details.getSubject());

        var file = new FileSystemResource(new File(details.getAttachment()));
        var fileName = file.getFilename();
        assert fileName != null;

        mimeMessageHelper.addAttachment(fileName, file);

        javaMailSender.send(mimeMessage);
    }
}
