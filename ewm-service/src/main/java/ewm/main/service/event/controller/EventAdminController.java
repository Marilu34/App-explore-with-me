package ewm.main.service.event.controller;

import ewm.main.service.event.dto.EventDto;
import ewm.main.service.event.dto.UpdateEventDto;
import ewm.main.service.event.model.Admin;
import ewm.main.service.event.service.EventService;
import ewm.main.service.validator.EventStartBefore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Validated
@Slf4j
public class EventAdminController {

    private final EventService eventsService;

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getEvents(@RequestParam(required = false) List<Long> users,
                                    @RequestParam(required = false) List<String> states,
                                    @RequestParam(required = false) List<Integer> categories,
                                    @RequestParam(name = "rangeStart", required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                    @RequestParam(name = "rangeEnd", required = false)
                                    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                    @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {

        Admin admin = Admin.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        log.info("Получены все мероприятия с правами аминистратора " + admin);
        return eventsService.getAdminEvent(admin);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEvent(@PathVariable Long eventId, @RequestBody @Valid @EventStartBefore UpdateEventDto updateEventDto) {
        log.info("Обновлено все мероприятие с правами аминистратора " + updateEventDto);

        return eventsService.updateEventAdmin(eventId, updateEventDto);
    }
}