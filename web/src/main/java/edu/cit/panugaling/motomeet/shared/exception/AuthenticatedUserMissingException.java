package edu.cit.panugaling.motomeet.shared.exception;

public class AuthenticatedUserMissingException extends RuntimeException {
    public AuthenticatedUserMissingException(String message) {
        super(message);
    }
}
