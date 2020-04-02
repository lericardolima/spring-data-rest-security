package com.example.demo.security.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public abstract class PasswordUtils {

    private static final Logger log = LoggerFactory.getLogger(PasswordUtils.class);

    public static String encode(String password) {
        log.info("Encrypting password!");
        return new BCryptPasswordEncoder().encode(password);
    }
}
