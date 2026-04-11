package edu.cit.panugaling.motomeet.service;

public class AuthenticatedUserMissingException extends RuntimeException {
    public AuthenticatedUserMissingException(String message) {
        super(message);
    }
}
