package com.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Random;


@Component
@Slf4j
@RequiredArgsConstructor
public class EmailNotificator {

    private Properties smtpSessionProperties;

    public static final String SMTP_PROPERTIES_PATH = "/smtp.properties";

    @PostConstruct
    public void afterPropertiesSet() {
        smtpSessionProperties = PropertyUtils.loadPropertyFile(SMTP_PROPERTIES_PATH);
    }
    public void send(String userMail, String code) throws IOException, MessagingException {


        Session mailSession = Session.getDefaultInstance(smtpSessionProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication
                        (smtpSessionProperties.getProperty("mail.user"),
                                smtpSessionProperties.getProperty("mail.password"));
            }
        });
        MimeMessage message = new MimeMessage(mailSession);
        message.setFrom(new InternetAddress(smtpSessionProperties.getProperty("mail.user")));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(userMail));
        message.setSubject("verification code");
        message.setText(generateMessage(code));
        Transport.send(message);
    }


    private String generateMessage(String code) {
        return "Your validation code is " + code;
    }

    public String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        for(int i =0; i < 5; i++) {
            Random r = new Random();
            int idx = r.nextInt(10);
            code.append(idx);
        }
        return code.toString();
    }
}
