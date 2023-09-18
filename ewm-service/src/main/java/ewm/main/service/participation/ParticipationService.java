package ewm.main.service.participation;

import ewm.main.service.common.models.Event;
import ewm.main.service.common.models.Status;
import ewm.main.service.event.EventRepository;
import ewm.main.service.event.EventService;
import ewm.main.service.exceptions.*;
import ewm.main.service.participation.model.Participation;
import ewm.main.service.participation.model.dto.EventRequestStatusUpdateRequest;
import ewm.main.service.participation.model.dto.EventRequestStatusUpdateResult;
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
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserService userService;

    public Participation getRequestById(long requestId) {
        Optional<Participation> optionalRequest = participationRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        } else {
            return optionalRequest.get();
        }
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param userId id пользователя
     * @return список заявок. В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    public List<Participation> getRequestsByUserId(long userId) {
        userService.getUserById(userId);
        return participationRepository.findByUser_IdOrderById(userId);
    }

    public List<Participation> getRequestsByEventIdAndInitiatorId(long eventId, long initiatorId) {
        eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        return participationRepository.findByEvent_IdOrderById(eventId);
    }

    /**
     * получить запрос на участие пользователя в событии
     *
     * @param eventId     id события
     * @param requesterId id пользователя-участника
     * @return объект Participation или null если не найдено
     */
    private Participation getRequestByEventIdAndRequesterId(long eventId, long requesterId) {
        eventService.getEventById(eventId);
        Optional<Participation> optionalRequest = participationRepository.findByEvent_IdAndUser_Id(eventId, requesterId);
        if (optionalRequest.isEmpty()) {
            return null;
        } else {
            return optionalRequest.get();
        }
    }

    public EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest, long eventId, long initiatorId) {
        ewm.main.service.event.model.Event event = eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        String newStatus = updateRequest.getStatus();
        int participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();

        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (participantLimit > 0 && event.getRequestModeration()) {

            //подтверждение заявки
            if ("CONFIRMED".equals(newStatus)) {
                //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
                if (confirmedRequests >= participantLimit) {
                    throw new ParticipationRequestLimitException("Достигнут лимит по заявкам на событие " + eventId);
                }
            }

            for (Long requestId : updateRequest.getRequestIds()) {
                Participation storageRequest = getRequestById(requestId);

                //статус можно изменить только у заявок, находящихся в состоянии ожидания
                if (Status.PENDING.toString().equals(storageRequest.getStatus())) {

                    //подтверждение заявки
                    if ("CONFIRMED".equals(newStatus)) {
                        if (confirmedRequests++ < participantLimit) {
                            storageRequest.setStatus(newStatus);
                            participationRepository.save(storageRequest);

                            //если при подтверждении данной заявки, лимит заявок для события исчерпан,
                            // то все неподтверждённые заявки необходимо отклонить
                            if (confirmedRequests == participantLimit) {
                                rejectAllPendingRequests(eventId);
                                break;
                            }
                        }
                    } else {
                        //отклонение заявки
                        storageRequest.setStatus(newStatus);
                        participationRepository.save(storageRequest);
                    }
                } else {
                    throw new ParticipationRequestInvalidStateException("Неверное состояние заявки " + requestId + " перед модерацией");
                }
            }
        }

        //обновить количество подтвержденных заявок на событие в БД
        EventRequestStatusUpdateResult updateResult = getEventRequestStatusUpdateResult(eventId);
        event.setConfirmedRequests(updateResult.getConfirmedRequests().size());
        eventRepository.save(event);

        return updateResult;
    }

    private void rejectAllPendingRequests(long eventId) {
        participationRepository.rejectAllPendingRequests(eventId);
    }

    private EventRequestStatusUpdateResult getEventRequestStatusUpdateResult(long eventId) {
        List<Participation> confirmed = participationRepository.findByEvent_IdAndStatusOrderById(eventId, Status.CONFIRMED.toString());
        List<Participation> rejected = participationRepository.findByEvent_IdAndStatusOrderById(eventId, Status.REJECTED.toString());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(confirmed))
                .rejectedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(rejected))
                .build();
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param eventId     id события
     * @param requesterId id пользователя
     * @return созданный объект Participation
     */
    public Participation createParticipationRequest(long eventId, long requesterId) {
        ewm.main.service.event.model.Event event = eventService.getEventById(eventId);
        User requester = userService.getUserById(requesterId);

        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (event.getInitiator().getId() == requesterId) {
            throw new ParticipationRequestInitiatorException("Инициатор события " + eventId + "не может добавить запрос на участие в своём событии");
        }

        //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getState() != Event.PUBLISHED) {
            throw new ParticipationRequestEventNotPublishedException("Событие " + eventId + "не опубликовано");
        }

        //нельзя добавить повторный запрос (Ожидается код ошибки 409)
        Participation oldRequest = getRequestByEventIdAndRequesterId(eventId, requesterId);
        if (oldRequest != null) {
            throw new ParticipationRequestDuplicationException("Нельзя добавить повторный запрос в событие" + eventId);
        }

        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipationRequestLimitReachedException("Достигнут лимит запросов на участие в событии " + eventId);
        }

        //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
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

    /**
     * Отмена своего запроса на участие в событии
     *
     * @param requestId id запроса
     * @param userId    id пользователя
     * @return объект Participation
     */
    public Participation cancelParticipationRequest(long requestId, long userId) {
        Participation request = getRequestById(requestId);
        if (request.getUser().getId() != userId) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        }

        String oldStatus = request.getStatus();
        request.setStatus(Status.CANCELED.toString());
        Participation storageRequest = participationRepository.save(request);

        //если заявка была подтверждена и это событие с ограничением участников и модерацией то уменьшить счетчик подтверждений
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