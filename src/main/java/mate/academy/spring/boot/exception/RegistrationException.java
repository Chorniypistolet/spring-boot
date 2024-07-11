package mate.academy.spring.boot.exception;

public class RegistrationException extends RuntimeException {
    public RegistrationException(String message) {
        super(message);
    }
}
