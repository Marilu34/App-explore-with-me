package ewm.main.service.participation.controller;

import ewm.main.service.participation.dto.ParticipationRequestDto;
import ewm.main.service.participation.model.EventRequestQuery;
import ewm.main.service.participation.model.EventRequestResponse;
import ewm.main.service.participation.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class ParticipationPrivateController {

    private final ParticipationService participationService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return participationService.addRequest(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getUserRequest(@PathVariable Long userId) {
        return participationService.getUserRequest(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return participationService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return participationService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestResponse changeRequestStatus(@PathVariable Long userId,
                                                    @PathVariable Long eventId,
                                                    @RequestBody(required = false) EventRequestQuery eventRequestQuery) {
        return participationService.changeRequestStatus(userId, eventId, eventRequestQuery);
    }
}