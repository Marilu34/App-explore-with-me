package ewm.main.service.exceptions;

public class ParticipationRequestNotFoundException extends RuntimeException {
    public ParticipationRequestNotFoundException(String message) {
        super(message);
    }
}