package com.example.demo.services.impl;

import com.example.demo.exceptions.UsernameAlreadyInUseException;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUsername(String username) {

        Optional<User> user = userRepository.findById(username);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Cannot find user with username equals to " + username);
        }

        return user.get();
    }

    @Override
    public User save(User user) throws UsernameAlreadyInUseException {

        Optional<User> userWithSameUsername = userRepository.findById(user.getUsername());
        if (userWithSameUsername.isPresent()) {
            throw new UsernameAlreadyInUseException(user.getUsername());
        }
        return userRepository.save(user);
    }
}
