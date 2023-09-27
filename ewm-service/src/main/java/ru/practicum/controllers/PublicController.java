package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;
import ru.practicum.services.CategoryService;
import ru.practicum.services.CompilationService;
import ru.practicum.services.EventService;
import ru.practicum.model.UsersRequest;
import ru.practicum.model.UserRequestResponse;
import ru.practicum.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
public class PublicController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final UserService userService;
    private final CompilationService compilationService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto postEvent(@PathVariable int userId, @RequestBody EventDtoRequest eventDto) {
        log.info("Cоздано новое событие от Пользователя: {}", userId);
        return userService.createEventForUser(userId, eventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventDto> getEventsForUser(@PathVariable int userId,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получены от Пользователя {} события", userId);
        return userService.getEventsForUser(userId, from, size);
    }
    @GetMapping("/users/{userId}/events/{eventId}")
    public EventDto getEventForUser(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Получено событие {} для Пользователя {}", eventId, userId);
        return userService.getEventForUser(userId, eventId);
    }
    @GetMapping("/events")
    public List<EventDto> getEventsByIP(@RequestParam(value = "text", defaultValue = "") String text,
                                        @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
                                        @RequestParam(value = "paid", required = false) Boolean paid,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                    @RequestParam(value = "rangeStart", required = false) LocalDateTime rangeStart,
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                    @RequestParam(value = "rangeEnd", required = false) LocalDateTime rangeEnd,
                                        @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort,
                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                        HttpServletRequest request) {
        log.info("Получить все События по ip: {}", request.getRemoteAddr());
        return eventService.getEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/events/{id}")
    public EventDto getEvent(@PathVariable Integer id, HttpServletRequest request) {
        log.info("Получить событие: {}", id);
        return eventService.getEvent(id, request);
    }


    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventDto updateEventForOwner(@PathVariable int userId, @PathVariable int eventId,
                                       @RequestBody EventDtoRequest eventDtoRequest) {
        log.info("Обновлено событие {} для Пользователя {}", eventId, userId);
        return userService.updateEventForOwner(userId, eventId, eventDtoRequest);
    }

    @GetMapping("/users/{userId}/requests")
    public List<UserRequestDto> getAllRequestForUser(@PathVariable int userId) {
        log.info("Получены все запросы для Пользователя {}", userId);
        return userService.getRequestsForUser(userId);
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public UserRequestDto postRequestForUser(@PathVariable int userId, @RequestParam int eventId) {
        log.info("Опубиковано Пользователем запрос для События {}", eventId);
        return userService.createRequestForEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public UserRequestDto updateCancelRequest(@PathVariable int userId, @PathVariable int requestId) {
        log.info("Окончен запрос с id = {}", requestId);
        return userService.cancelUserRequestForEvent(userId, requestId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<UserRequestDto> getAllRequestForOwnerEvent(@PathVariable int userId, @PathVariable int eventId) {
        log.info("Получены все запросы от Пользователя {} на События {}", userId, eventId);
        return userService.getAllRequestsForOwnerEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public UserRequestResponse updateRequestForOwnerEvent(@PathVariable int userId, @PathVariable int eventId,
                                                          @RequestBody UsersRequest format) {
        log.info("Change status UserRequest: {}", format);
        return userService.changeStatusRequestForEvent(userId, eventId, format);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(value = "pinned", defaultValue = "false") Boolean pinned,
                                                   @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получить все компиляции: ");
        return compilationService.getAllCompilation(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable Integer compId) {
        log.info("Получить компиляцию: {}", compId);
        return compilationService.getCompilation(compId);
    }
    @GetMapping("/categories")
    public List<CategoryDto> getAllCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получить все категории");
        return categoryService.getAllCategory(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategory(@PathVariable Integer id) {
        log.info("Получить категорию: {}", id);
        return categoryService.getCategory(id);
    }



    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentaryDto postComment(@PathVariable int userId, @PathVariable int eventId,
                                     @RequestBody CommentaryDto commentaryDto) {
        log.info("Оставить комментарий {} для пользователя {}", commentaryDto, userId);
        return userService.createComment(userId, eventId, commentaryDto);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentaryDto updateComment(@PathVariable int userId, @PathVariable int commentId,
                                      @RequestBody CommentaryDto commentaryDto) {
        log.info("Изменить комментарий {} для Пользователя {}", commentaryDto, userId);
        return userService.updateComment(userId, commentId, commentaryDto);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable int userId, @PathVariable int commentId) {
        log.info("Удалить комментарий {} для Пользователя {}", commentId, userId);
        userService.deleteComment(userId, commentId);
    }

}