package ewm.main.service.event;

import ewm.main.service.event.model.dto.*;
import ewm.main.service.participation.ParticipationService;
import ewm.main.service.participation.model.dto.EventRequestQuery;
import ewm.main.service.participation.model.dto.EventRequestResponse;
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


    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@Valid @RequestBody EventDtoUpdated eventDtoUpdated,
                                @Positive @PathVariable long userId) {
        log.info("Создано новое событие {} для Пользователя с id = {}", eventDtoUpdated, userId);
        return EventDtoMapper.toEventFullDto(eventService.createEvent(eventDtoUpdated, userId));
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventByIdAndInitiatorId(@Positive @PathVariable long userId, @Positive @PathVariable long eventId) {
        log.info("Получено Событие: userId = {}, eventId = {}", userId, eventId);
        return EventDtoMapper.toEventFullDto(eventService.getEventByIdAndInitiatorId(eventId, userId));
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@Positive @PathVariable long userId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получить все события для пользователя: userId = {}, from = {}, size = {}", userId, from, size);
        return EventDtoMapper.toEventShortDtoList(eventService.getUserEvents(userId, from, size));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEventByInitiator(@RequestBody EventUserRequest eventUserRequest,
                                           @Positive @PathVariable long userId,
                                           @Positive @PathVariable long eventId) {
        log.info("Обновлено для Пользователя Событие : userId = {}, eventId = {}, {}", userId, eventId, eventUserRequest);
        return EventDtoMapper.toEventFullDto(eventService.updateEventByInitiator(eventUserRequest, eventId, userId));
    }


    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestResponse updateRequestsStatus(@Valid @RequestBody EventRequestQuery updateRequest,
                                                     @Positive @PathVariable long userId,
                                                     @Positive @PathVariable long eventId) {
        log.info("Обновить заявки на участие в мероприятии: userId = {}, eventId = {}, updateRequest = {}", userId, eventId, updateRequest);
        return participationService.updateRequestsStatus(updateRequest, eventId, userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByEventIdAndInitiatorId(@Positive @PathVariable long userId,
                                                                            @Positive @PathVariable long eventId) {
        log.info("Получать заявки на участие в мероприятии: userId = {}, eventId = {}", userId, eventId);
        return ParticipationDtoMapper.toParticipationRequestDtoList(participationService.getRequestsByEventIdAndInitiatorId(eventId, userId));
    }
}