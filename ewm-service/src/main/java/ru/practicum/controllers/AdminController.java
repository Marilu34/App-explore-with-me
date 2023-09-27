package ru.practicum.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.services.CategoryService;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationDtoRequest;
import ru.practicum.services.CompilationService;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventDtoRequest;
import ru.practicum.model.State;
import ru.practicum.services.EventService;
import ru.practicum.dto.UserDto;
import ru.practicum.services.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final CompilationService compilationService;


    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody UserDto userDto) {
        log.info("Создан Пользователь: {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/users/{id}")
    public UserDto updateUser(@PathVariable Integer id, @RequestBody UserDto user) {
        log.info("Информация о Пользователе Обновлена: {}", user);
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserDto deleteUser(@PathVariable Integer userId) {
        log.info("Пользователь удален: {}", userId);
        return userService.deleteUser(userId);
    }


    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(value = "ids", defaultValue = "0") List<Integer> ids,
                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                  @RequestParam(value = "size", defaultValue = "10")
                                  Integer size) {
        log.info("Get users");
        return userService.getUsers(ids, from, size);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody CategoryDto categoryDto) {
        log.info("Создана категория: {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @PatchMapping("/categories/{id}")
    public CategoryDto updateCategory(@PathVariable Integer id, @RequestBody CategoryDto categoryDto) {
        log.info("Категория Обновлена: {}", categoryDto);
        return categoryService.updateCategory(id, categoryDto);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CategoryDto deleteCategory(@PathVariable Integer id) {
        log.info("Категория удалена: {}", id);
        return categoryService.deleteCategory(id);
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateEvent(@PathVariable Integer eventId, @RequestBody EventDtoRequest eventDtoRequest) {
        return eventService.patchAdminEvent(eventId, eventDtoRequest);
    }

    @GetMapping("/events")
    public List<EventDto> getEvents(@RequestParam(value = "users", defaultValue = "") List<Long> users,
                                    @RequestParam(value = "states", defaultValue = "") List<State> states,
                                    @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
                                    @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                    @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilations(@RequestBody CompilationDtoRequest compilationDtoRequest) {
        return compilationService.createCompilation(compilationDtoRequest);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Integer compId,
                                            @RequestBody CompilationDtoRequest compilationDtoRequest) {
        return compilationService.updateCompilation(compId, compilationDtoRequest);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        compilationService.deleteCompilation(compId);
    }
}