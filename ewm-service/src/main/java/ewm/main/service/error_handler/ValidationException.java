package ewm.main.service.error_handler;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
