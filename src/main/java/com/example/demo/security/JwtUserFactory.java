package com.example.demo.security;

import com.example.demo.models.User;

public class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(user.getUsername(), user.getPassword(), user.getRole());
    }

}
