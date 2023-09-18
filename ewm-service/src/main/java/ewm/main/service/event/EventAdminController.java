package ewm.main.service.event;

import ewm.main.service.common.models.State;
import ewm.main.service.event.model.dto.EventDtoMapper;
import ewm.main.service.event.model.dto.EventDto;
import ewm.main.service.event.model.dto.EventAdminRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ewm.main.service.common.EwmConstants.DATE_TIME_FORMAT;


@RestController
@RequestMapping(path = "/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventAdminController {
    private final EventService eventService;


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto update(@RequestBody EventAdminRequest updateRequest, @Positive @PathVariable long eventId) {
        log.info("Администратор обновил событие: {}, eventId = {}", updateRequest, eventId);
        return EventDtoMapper.toEventDto(eventService.updateEventByAdmin(updateRequest, eventId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getAllEvents(@RequestParam(name = "users", defaultValue = "") List<Integer> usersIdList,
                                       @RequestParam(name = "states", defaultValue = "") List<State> states,
                                       @RequestParam(name = "categories", defaultValue = "") List<Integer> categoriesIdList,
                                       @RequestParam(defaultValue = "") @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                       @RequestParam(defaultValue = "") @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                       @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Администратор получает все события: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);

        return EventDtoMapper.toEventDtoList(eventService.getAllEventsAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size));
    }
}
