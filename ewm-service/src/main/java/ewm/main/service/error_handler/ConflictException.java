package ewm.main.service.error_handler;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
