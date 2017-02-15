package ee.nipt.service;


import ee.nipt.configuration.MailConfiguration;
import ee.nipt.domain.Examination;
import ee.nipt.domain.Person;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Principal;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MailServiceImpl implements MailService {
    private final SpringTemplateEngine templateEngine;
    private final MailConfiguration mailConfiguration;


    @Override
    public void emailUserCreate(Person newPerson, String password, Person creator) throws MessagingException {
        final Context ctx = new Context();
        ctx.setVariable("creator", creator);
        ctx.setVariable("password", password);
        ctx.setVariable("newPerson", newPerson);
        final String htmlContent = templateEngine.process("user_email", ctx);
        send("NIPT kasutajatunnus", htmlContent, newPerson.getEmail());
    }

    @Override
    public void emailNotifyResults(Examination examination, Principal assessor, Person doctor) throws MessagingException {
        final Context ctx = new Context();
        ctx.setVariable("examination", examination);
        ctx.setVariable("doctor", doctor);
        final String htmlContent = templateEngine.process("notification_email", ctx);
        send("NIPT tulemused " + examination.getSampleNumber(), htmlContent, examination.getEmail());
    }

    private void send(String subject, String htmlContent, String to) throws MessagingException {
        JavaMailSender sender = mailConfiguration.getJavaMailSender();
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setTo(to);
        try {
            helper.setText(htmlContent, true);
            helper.setSubject(subject);
            sender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send E-mail with subject {} to {}", subject, to);
            throw e;
        }
    }
}
