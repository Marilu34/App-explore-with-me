package ewm.main.service.participation.service;

import ewm.main.service.participation.dto.ParticipationRequestDto;
import ewm.main.service.participation.model.EventRequestQuery;
import ewm.main.service.participation.model.EventRequestResponse;

import java.util.List;

public interface ParticipationService {
    ParticipationRequestDto addRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getUserRequest(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestResponse changeRequestStatus(Long userId, Long eventId, EventRequestQuery eventRequestQuery);
}