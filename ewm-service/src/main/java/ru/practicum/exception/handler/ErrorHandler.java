package ru.practicum.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.exception.error.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class,
            HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse ConflictException(final RuntimeException exception) {
        log.error("ERROR 409: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.getReasonPhrase(),
                "Для запрошенной операции условия не выполнены.",
                exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse NotFoundException(final NotFoundException exception) {
        log.error("ERROR 404: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), "Требуемый объект не был найден.",
                exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({ConstraintViolationException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse ValidationException(final RuntimeException exception) {
        log.error("ERROR 400: {}", exception.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), "Неправильно сделанный запрос.",
                exception.getMessage(), LocalDateTime.now());
    }
}