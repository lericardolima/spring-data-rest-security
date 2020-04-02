package com.example.demo.exceptions;

public class UsernameAlreadyInUseException extends Exception {
    public UsernameAlreadyInUseException(String username) {
        super("Username " + username + " already in use.");
    }
}
