package ewm.main.service.event.controller;

import ewm.main.service.event.dto.EventDto;
import ewm.main.service.event.dto.EventShortDto;
import ewm.main.service.event.dto.NewEventDto;
import ewm.main.service.event.dto.UpdateEventDto;
import ewm.main.service.event.service.EventService;
import ewm.main.service.validator.EventStartBefore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Validated
@Slf4j
public class EventPrivateController {

    private final EventService eventsService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable Long userId, @EventStartBefore(min = 2) @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Созданано мероприятие " + newEventDto);

        return eventsService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(required = false, defaultValue = "10") @Positive int size) {
        log.info("Получены все мероприятия с правами пользователя " + userId);

        return eventsService.getUserEvents(userId, from, size);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable Long userId,
                                @PathVariable Long eventId,
                                @RequestBody @Valid @EventStartBefore(min = 2) UpdateEventDto updateEventDto) {
        log.info("Обновлено мероприятие с правами пользователя " + updateEventDto);

        return eventsService.updateEvent(userId, eventId, updateEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventById(@PathVariable Long userId,
                                 @PathVariable Long eventId) {
        log.info("Получено  мероприятие " + eventId + " с правами пользователя " + userId);

        return eventsService.getUserEventById(userId, eventId);
    }
}