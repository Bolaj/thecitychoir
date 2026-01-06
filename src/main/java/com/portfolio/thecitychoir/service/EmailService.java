package com.portfolio.thecitychoir.service;


import com.portfolio.thecitychoir.entity.ProfileEntity;
import com.portfolio.thecitychoir.entity.RehearsalEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor

public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;


    @Async
    public void sendWelcomeEmail(ProfileEntity user) throws MessagingException, UnsupportedEncodingException {

        String activationUrl = "http://localhost:8081/api/auth/activate?token=" + user.getActivationToken();

        String html = """
            <html>
            <body style="font-family:Arial,sans-serif; color:#222;">
                <h2> Welcome to The 500 City Choir, %s!</h2>
                <p>Your registration number is <b>%s</b></p>

                <p>Please <b>activate your account</b> by clicking the button below:</p>

                <p style="margin-top:20px;">
                    <a href="%s" style="background:#1464F4; padding:10px 18px; 
                    color:white; text-decoration:none; border-radius:5px;">
                        Activate My Account
                    </a>
                </p>

                <br/>
                <p>We are glad to have you on board! </p>
            </body>
            </html>
        """.formatted(user.getFullName(), user.getRegistrationNumber(), activationUrl);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(user.getEmail());
        helper.setFrom(fromEmail, fromName);
        helper.setSubject("Welcome to The City Choir");
        helper.setText(html, true);

        mailSender.send(message);
    }
    @Async
    public void sendRehearsalNotification(String recipientEmail, String fullName, RehearsalEntity rehearsal)
            throws MessagingException, UnsupportedEncodingException {

        String html = """
        <html>
        <body style="font-family:Arial,sans-serif; color:#222;">
            <h2>New Rehearsal Scheduled! 🎵</h2>
            <p>Hi %s,</p>
            <p>A new rehearsal has been scheduled: <b>%s</b></p>
            
            <div style="background:#f4f4f4; padding:15px; border-radius:8px;">
                <p>📅 <b>Date:</b> %s</p>
                <p>⏰ <b>Time:</b> %s - %s</p>
            </div>

            <p>Please ensure you are present and on time. Check the app for location details!</p>
            <br/>
            <p>Best regards,<br/>The City Choir Management</p>
        </body>
        </html>
    """.formatted(
                fullName,
                rehearsal.getName(),
                rehearsal.getRehearsalDate(),
                rehearsal.getStartTime().toLocalTime(),
                rehearsal.getEndTime().toLocalTime()
        );

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(recipientEmail);
        helper.setFrom(fromEmail, fromName);
        helper.setSubject("New Rehearsal: " + rehearsal.getName());
        helper.setText(html, true);

        mailSender.send(message);
    }
}
