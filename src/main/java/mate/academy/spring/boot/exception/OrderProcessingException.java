package mate.academy.spring.boot.exception;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String massage) {
        super(massage);
    }
}
