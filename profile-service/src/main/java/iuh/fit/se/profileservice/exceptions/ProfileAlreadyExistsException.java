package iuh.fit.se.profileservice.exceptions;

public class ProfileAlreadyExistsException extends Exception {
    public ProfileAlreadyExistsException(String message) {
        super(message);
    }
}