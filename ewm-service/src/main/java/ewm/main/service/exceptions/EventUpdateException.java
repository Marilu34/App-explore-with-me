package ewm.main.service.exceptions;

//ожидаемый код 409 CONFLICT

public class EventUpdateException extends RuntimeException {
    public EventUpdateException(String message) {
        super(message);
    }
}
