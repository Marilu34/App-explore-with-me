package ewm.main.service.exceptions;

public class ParticipationRequestLimitException extends RuntimeException {
    public ParticipationRequestLimitException(String message) {
        super(message);
    }
}
