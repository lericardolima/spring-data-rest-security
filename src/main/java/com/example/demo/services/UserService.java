package com.example.demo.services;

import com.example.demo.models.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByUsername(String username);

}
