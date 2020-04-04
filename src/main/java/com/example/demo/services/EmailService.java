package com.example.demo.services;

import javax.mail.internet.MimeMessage;

public interface EmailService {

    void send(MimeMessage mail);

    MimeMessage getMimeMessage();
}
