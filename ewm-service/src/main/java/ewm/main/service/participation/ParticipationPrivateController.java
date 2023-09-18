package ewm.main.service.participation;


import ewm.main.service.participation.model.dto.ParticipationDtoMapper;
import ewm.main.service.participation.model.dto.ParticipationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ParticipationPrivateController {
    private final ParticipationService participationService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@Positive @PathVariable long userId,
                                                              @Positive @RequestParam long eventId) {
        log.info("Создать заявку на участие: userId = {}, eventId = {}", userId, eventId);
        return ParticipationDtoMapper.toParticipationRequestDto(participationService.createParticipationRequest(eventId, userId));
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserId(@Positive @PathVariable long userId) {
        log.info("Получать все запросы для пользователя: userId = {}", userId);
        return ParticipationDtoMapper.toParticipationRequestDtoList(participationService.getRequestsByUserId(userId));
    }


    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelParticipationRequest(@Positive @PathVariable long userId,
                                                              @Positive @PathVariable long requestId) {
        log.info("Отменить заявку на участие: userId = {}, requestId = {}", userId, requestId);
        return ParticipationDtoMapper.toParticipationRequestDto(participationService.cancelParticipationRequest(requestId, userId));
    }
}