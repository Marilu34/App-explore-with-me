package ewm.main.service.exceptions;

//ожидаемый код 409 CONFLICT

public class CategoryNameNotUniqueException extends RuntimeException {
    public CategoryNameNotUniqueException(String message) {
        super(message);
    }
}
