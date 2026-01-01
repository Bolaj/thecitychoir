package com.portfolio.thecitychoir.exceptions;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("Email is already registered: " + email);
    }
}
