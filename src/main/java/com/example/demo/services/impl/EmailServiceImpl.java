package com.example.demo.services.impl;

import com.example.demo.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(MimeMessage mail) {
        mailSender.send(mail);
    }

    @Override
    public MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }
}
