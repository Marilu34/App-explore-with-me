package ewm.main.service.participation;

import ewm.main.service.common.models.State;
import ewm.main.service.common.models.Status;
import ewm.main.service.event.EventRepository;
import ewm.main.service.event.EventService;
import ewm.main.service.exceptions.*;
import ewm.main.service.participation.model.Participation;
import ewm.main.service.participation.model.dto.EventRequestQuery;
import ewm.main.service.participation.model.dto.EventRequestResponse;
import ewm.main.service.participation.model.dto.ParticipationDtoMapper;
import ewm.main.service.user.UserService;
import ewm.main.service.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final EventService eventService;
    private final UserService userService;
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;

    public Participation createParticipationRequest(long eventId, long requesterId) {
        ewm.main.service.event.model.Event event = eventService.getEventById(eventId);
        User requester = userService.getUserById(requesterId);

        if (event.getInitiator().getId() == requesterId) {
            throw new ParticipationRequestInitiatorException("Инициатор события " + eventId + "не может добавить запрос на участие в своём событии");
        }

        if (event.getState() != State.PUBLISHED) {
            throw new ParticipationRequestEventNotPublishedException("Событие " + eventId + "не опубликовано");
        }

        Participation oldRequest = getRequestByEventIdAndRequesterId(eventId, requesterId);
        if (oldRequest != null) {
            throw new ParticipationRequestDuplicationException("Повторно нельзя создать запрос в событие" + eventId);
        }

        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipationRequestLimitReachedException("Достигнут лимит запросов на участие в событии " + eventId);
        }

        String newStatus;
        if (event.getParticipantLimit() > 0 && event.getRequestModeration()) {
            newStatus = Status.PENDING.toString();
        } else {
            newStatus = Status.CONFIRMED.toString();
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        Participation request = Participation.builder()
                .created(LocalDateTime.now())
                .event(event)
                .user(requester)
                .status(newStatus)
                .build();

        return participationRepository.save(request);
    }
    public EventRequestResponse updateRequestsStatus(EventRequestQuery updateRequest, long eventId, long initiatorId) {
        ewm.main.service.event.model.Event event = eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        String newStatus = updateRequest.getStatus();
        int participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();

        if (participantLimit > 0 && event.getRequestModeration()) {

            if ("CONFIRMED".equals(newStatus)) {
                if (confirmedRequests >= participantLimit) {
                    throw new ParticipationRequestLimitException("Достигнут лимит по заявкам на событие " + eventId);
                }
            }

            for (Long requestId : updateRequest.getRequestIds()) {
                Participation storageRequest = getRequestById(requestId);

                if (Status.PENDING.toString().equals(storageRequest.getStatus())) {

                    if ("CONFIRMED".equals(newStatus)) {
                        if (confirmedRequests++ < participantLimit) {
                            storageRequest.setStatus(newStatus);
                            participationRepository.save(storageRequest);

                            if (confirmedRequests == participantLimit) {
                                confirmationRequest(eventId);
                                break;
                            }
                        }
                    } else {
                        storageRequest.setStatus(newStatus);
                        participationRepository.save(storageRequest);
                    }
                } else {
                    throw new ParticipationRequestInvalidStateException("Неверное состояние заявки " + requestId + " перед модерацией");
                }
            }
        }

        EventRequestResponse updateResult = getEventRequestStatusUpdateResult(eventId);
        event.setConfirmedRequests(updateResult.getConfirmedRequests().size());
        eventRepository.save(event);

        return updateResult;
    }
    public Participation getRequestById(long requestId) {
        Optional<Participation> optionalRequest = participationRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        } else {
            return optionalRequest.get();
        }
    }


    public List<Participation> getRequestsByUserId(long userId) {
        userService.getUserById(userId);
        return participationRepository.findByUser_IdOrderById(userId);
    }

    public List<Participation> getRequestsByEventIdAndInitiatorId(long eventId, long initiatorId) {
        eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        return participationRepository.findByEvent_IdOrderById(eventId);
    }


    private Participation getRequestByEventIdAndRequesterId(long eventId, long requesterId) {
        eventService.getEventById(eventId);
        Optional<Participation> optionalRequest = participationRepository.findByEvent_IdAndUser_Id(eventId, requesterId);
        if (optionalRequest.isEmpty()) {
            return null;
        } else {
            return optionalRequest.get();
        }
    }


    private void confirmationRequest(long eventId) {
        participationRepository.rejectAllPendingRequests(eventId);
    }

    private EventRequestResponse getEventRequestStatusUpdateResult(long eventId) {
        List<Participation> confirmed = participationRepository.findByEvent_IdAndStatusOrderById(eventId, Status.CONFIRMED.toString());
        List<Participation> rejected = participationRepository.findByEvent_IdAndStatusOrderById(eventId, Status.REJECTED.toString());

        return EventRequestResponse.builder()
                .confirmedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(confirmed))
                .rejectedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(rejected))
                .build();
    }



    public Participation cancelParticipationRequest(long requestId, long userId) {
        Participation request = getRequestById(requestId);
        if (request.getUser().getId() != userId) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        }

        String oldStatus = request.getStatus();
        request.setStatus(Status.CANCELED.toString());
        Participation storageRequest = participationRepository.save(request);

        if (Status.CONFIRMED.toString().equals(oldStatus)) {
            ewm.main.service.event.model.Event event = storageRequest.getEvent();
            if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
                event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            }
            eventRepository.save(event);
        }
        return storageRequest;
    }
}