package ewm.stats.server.handler;

import ewm.stats.server.exception.WrongTimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(WrongTimeException.class)
    public ResponseEntity<String> handleWrongTimeException(WrongTimeException e) {
        return new ResponseEntity<>("WrongTimeException", HttpStatus.BAD_REQUEST);
    }
}