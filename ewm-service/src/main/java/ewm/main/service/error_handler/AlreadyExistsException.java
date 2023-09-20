package ewm.main.service.error_handler;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {

        super(message);
    }
}
