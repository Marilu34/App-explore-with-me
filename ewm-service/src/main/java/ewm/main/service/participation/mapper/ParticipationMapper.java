package ewm.main.service.participation.mapper;

import ewm.main.service.participation.dto.ParticipationRequestDto;
import ewm.main.service.participation.model.EventRequestResponse;
import ewm.main.service.participation.model.ParticipationRequest;
import ewm.main.service.participation.model.ParticipationStatus;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ParticipationMapper {
    public ParticipationRequestDto toParticipationEventDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent().getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatus())
                .build();
    }

    public EventRequestResponse toEventRequestStatusUpdateResult(List<ParticipationRequest> participationRequestList) {
        return EventRequestResponse.builder()
                .confirmedRequests(participationRequestList.stream()
                        .filter(request -> request.getStatus().equals(ParticipationStatus.CONFIRMED))
                        .map(ParticipationMapper::toParticipationEventDto)
                        .collect(Collectors.toList()))
                .rejectedRequests(participationRequestList.stream()
                        .filter(request -> request.getStatus().equals(ParticipationStatus.REJECTED))
                        .map(ParticipationMapper::toParticipationEventDto)
                        .collect(Collectors.toList()))
                .build();
    }
}