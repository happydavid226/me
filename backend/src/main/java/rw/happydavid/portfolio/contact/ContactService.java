package rw.happydavid.portfolio.contact;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ContactService {
    private final JavaMailSender mailSender;
    private final String from;
    private final String to;
    private final Clock clock = Clock.systemUTC();

    public ContactService(JavaMailSender mailSender,
                          @Value("${portfolio.mail.from}") String from,
                          @Value("${portfolio.contact.to}") String to) {
        this.mailSender = mailSender; this.from = from; this.to = to;
    }

    public void send(ContactRequest request) {
        String name = request.name().trim();
        String email = request.email().trim();
        String body = request.message().trim();
        OffsetDateTime submittedAt = OffsetDateTime.now(clock);
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from); mail.setTo(to); mail.setReplyTo(email);
        mail.setSubject("Portfolio contact from " + name.replaceAll("[\\r\\n]", " "));
        mail.setText("New portfolio message\n\nFrom: " + name + "\nEmail: " + email +
                "\nSubmitted: " + submittedAt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) +
                "\n\nMessage:\n" + body);
        mailSender.send(mail);
    }
}
