package com.example.project_web.model.exceptions;

public class InvalidUserCredentials extends RuntimeException {

    public InvalidUserCredentials() {
        super("Invalid user credentials exception");
    }
}
