package ewm.main.service.event;

import ewm.main.service.event.model.dto.*;
import ewm.main.service.participation.ParticipationService;
import ewm.main.service.participation.model.dto.EventRequestStatusUpdateRequest;
import ewm.main.service.participation.model.dto.EventRequestStatusUpdateResult;
import ewm.main.service.participation.model.dto.ParticipationDtoMapper;
import ewm.main.service.participation.model.dto.ParticipationRequestDto;
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
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final ParticipationService participationService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@Positive @PathVariable long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get all events for user: userId = {}, from = {}, size = {}", userId, from, size);
        return EventDtoMapper.toEventShortDtoList(eventService.getUserEvents(userId, from, size));
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto,
                                    @Positive @PathVariable long userId) {
        log.info("Create new event {} for userId {}", newEventDto, userId);
        return EventDtoMapper.toEventFullDto(eventService.createEvent(newEventDto, userId));
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdAndInitiatorId(@Positive @PathVariable long userId, @Positive @PathVariable long eventId) {
        log.info("Get event: userId = {}, eventId = {}", userId, eventId);
        return EventDtoMapper.toEventFullDto(eventService.getEventByIdAndInitiatorId(eventId, userId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByInitiator(@RequestBody UpdateEventUserRequest updateEventUserRequest,
                                               @Positive @PathVariable long userId,
                                               @Positive @PathVariable long eventId) {
        log.info("Update user event: userId = {}, eventId = {}, {}", userId, eventId, updateEventUserRequest);
        return EventDtoMapper.toEventFullDto(eventService.updateEventByInitiator(updateEventUserRequest, eventId, userId));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByEventIdAndInitiatorId(@Positive @PathVariable long userId,
                                                                            @Positive @PathVariable long eventId) {
        log.info("Get participation requests for event: userId = {}, eventId = {}", userId, eventId);
        return ParticipationDtoMapper.toParticipationRequestDtoList(participationService.getRequestsByEventIdAndInitiatorId(eventId, userId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsStatus(@Valid @RequestBody EventRequestStatusUpdateRequest updateRequest,
                                                               @Positive @PathVariable long userId,
                                                               @Positive @PathVariable long eventId) {
        log.info("Update participation requests for event: userId = {}, eventId = {}, updateRequest = {}", userId, eventId, updateRequest);
        return participationService.updateRequestsStatus(updateRequest, eventId, userId);
    }
}