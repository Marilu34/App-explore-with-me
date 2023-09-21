package ewm.main.service.exception;

public class WrongEventStateException extends RuntimeException {

    public WrongEventStateException(String message) {
        super(message);
    }
}