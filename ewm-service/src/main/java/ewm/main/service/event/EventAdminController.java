package ewm.main.service.event;

import ewm.main.service.common.models.EventState;
import ewm.main.service.event.model.dto.EventDtoMapper;
import ewm.main.service.event.model.dto.EventFullDto;
import ewm.main.service.event.model.dto.UpdateEventAdminRequest;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllEvents(@RequestParam(name = "users", defaultValue = "") List<Integer> usersIdList,
                                           @RequestParam(name = "states", defaultValue = "") List<EventState> states,
                                           @RequestParam(name = "categories", defaultValue = "") List<Integer> categoriesIdList,
                                           @RequestParam(defaultValue = "") @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                           @RequestParam(defaultValue = "") @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                           @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Admin get all events: users = {}, states = {}, categories = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);

        return EventDtoMapper.toEventFullDtoList(eventService.getAllEventsAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@RequestBody UpdateEventAdminRequest updateRequest, @Positive @PathVariable long eventId) {
        log.info("Admin update event: {}, eventId = {}", updateRequest, eventId);
        return EventDtoMapper.toEventFullDto(eventService.updateEventByAdmin(updateRequest, eventId));
    }
}
