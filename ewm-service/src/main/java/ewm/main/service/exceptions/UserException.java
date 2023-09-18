package ewm.main.service.exceptions;

//ожидаемый код 409 CONFLICT

public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
}