package ewm.main.service.exceptions;

//ожидаемый код 409 CONFLICT

public class UserEmailNotUniqueException extends RuntimeException {
    public UserEmailNotUniqueException(String message) {
        super(message);
    }
}