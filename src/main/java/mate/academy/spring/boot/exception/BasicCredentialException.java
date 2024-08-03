package mate.academy.spring.boot.exception;

public class BasicCredentialException extends RuntimeException{
    public BasicCredentialException(String message) {
        super(message);
    }
}
