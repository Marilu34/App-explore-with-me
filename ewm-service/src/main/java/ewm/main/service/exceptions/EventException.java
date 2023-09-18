package ewm.main.service.exceptions;

//ожидаемый код 409 CONFLICT

public class EventException extends RuntimeException {
    public EventException(String message) {
        super(message);
    }
}
