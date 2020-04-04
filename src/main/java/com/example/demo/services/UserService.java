package com.example.demo.services;

import com.example.demo.exceptions.EmailAlreadyInUseException;
import com.example.demo.exceptions.UsernameAlreadyInUseException;
import com.example.demo.models.User;

public interface UserService {

    User findByUsername(String username);

    User save(User user) throws UsernameAlreadyInUseException, EmailAlreadyInUseException;
}
