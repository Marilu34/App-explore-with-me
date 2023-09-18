package ewm.main.service.event;

import ewm.main.service.category.CategoryRepository;
import ewm.main.service.common.EwmConstants;
import ewm.main.service.common.models.Event;
import ewm.main.service.event.model.dto.NewEventDto;
import ewm.main.service.event.model.dto.UpdateEventAdminRequest;
import ewm.main.service.event.model.dto.UpdateEventUserRequest;
import ewm.main.service.exceptions.EventNotFoundException;
import ewm.main.service.exceptions.EventUpdateException;
import ewm.main.service.user.UserService;
import ewm.stats.client.StatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventRepositoryImpl eventRepositoryImpl;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final StatClient statClient;

    public ewm.main.service.event.model.Event getEventById(long eventId) {
        Optional<ewm.main.service.event.model.Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено");
        } else {
            return optionalEvent.get();
        }
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     *
     * @param eventId id события
     * @return полную информацию о событии
     */
    public ewm.main.service.event.model.Event getEventByIdPublic(long eventId, String requestUri, String remoteIp) {
        ewm.main.service.event.model.Event event = getEventById(eventId);
        //событие должно быть опубликовано
        if (event.getState() != Event.PUBLISHED) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено");
        }

        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statClient.newStat(requestUri, remoteIp, LocalDateTime.now());
        return event;
    }

    public int getEventsCountByCategoryId(long categoryId) {
        return eventRepository.getEventsCountByCategoryId(categoryId);
    }

    public List<ewm.main.service.event.model.Event> getAllEventsAdmin(List<Integer> usersIdList,
                                                                      List<Event> states,
                                                                      List<Integer> categoriesIdList,
                                                                      LocalDateTime rangeStart,
                                                                      LocalDateTime rangeEnd,
                                                                      int from,
                                                                      int size) {
        return eventRepositoryImpl.findAllEventsByFilterAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);
    }

    /**
     * Получение событий с возможностью фильтрации, публичный эндпоинт
     *
     * @param text             текст для поиска в содержимом аннотации и подробном описании события
     * @param categoriesIdList список идентификаторов категорий в которых будет вестись поиск
     * @param paid             поиск только платных/бесплатных событий
     * @param rangeStart       дата и время не раньше которых должно произойти событие
     * @param rangeEnd         дата и время не позже которых должно произойти событие
     * @param onlyAvailable    только события у которых не исчерпан лимит запросов на участие
     * @param sort             Вариант сортировки: по дате события или по количеству просмотров, аvailable values : EVENT_DATE, VIEWS
     * @param from             количество событий, которые нужно пропустить для формирования текущего набора
     * @param size             количество событий в наборе
     * @return список событий Event
     */
    public List<ewm.main.service.event.model.Event> getAllEventsPublic(String text,
                                                                       List<Integer> categoriesIdList,
                                                                       Boolean paid,
                                                                       LocalDateTime rangeStart,
                                                                       LocalDateTime rangeEnd,
                                                                       Boolean onlyAvailable,
                                                                       String sort,
                                                                       int from,
                                                                       int size,
                                                                       String requestUri,
                                                                       String remoteIp) {
        //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
        statClient.newStat(requestUri, remoteIp, LocalDateTime.now());

        return eventRepositoryImpl.findAllEventsByFilterPublic(text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    public ewm.main.service.event.model.Event updateEventByAdmin(UpdateEventAdminRequest updateRequest, long eventId) {
        ewm.main.service.event.model.Event storageEvent = getEventById(eventId);

        //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        if (updateRequest.getStateAction() != null && "REJECT_EVENT".equals(updateRequest.getStateAction())) {
            if (storageEvent.getState() != Event.PENDING) {
                throw new EventUpdateException("Cannot cancel the event because it's not in the right state: " + storageEvent.getState().toString());
            } else {
                storageEvent.setState(Event.CANCELED);
            }
        }

        //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
        if (updateRequest.getStateAction() != null && "PUBLISH_EVENT".equals(updateRequest.getStateAction())) {
            if (storageEvent.getState() != Event.PENDING) {
                throw new EventUpdateException("Cannot publish the event because it's not in the right state: " + storageEvent.getState().toString());
            } else {
                storageEvent.setState(Event.PUBLISHED);
                storageEvent.setPublishedOn(LocalDateTime.now());
            }
        }

        //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
        if (updateRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), EwmConstants.DATE_TIME_FORMATTER);
            if (newEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            } else {
                storageEvent.setEventDate(newEventDate);
            }
        }

        if (updateRequest.getAnnotation() != null) {
            storageEvent.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null) {
            storageEvent.setCategory(categoryRepository.getReferenceById(updateRequest.getCategory()));
        }

        if (updateRequest.getDescription() != null) {
            storageEvent.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getLocation() != null) {
            storageEvent.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null) {
            storageEvent.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            storageEvent.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            storageEvent.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            storageEvent.setTitle(updateRequest.getTitle());
        }

        return eventRepository.save(storageEvent);
    }

    public List<ewm.main.service.event.model.Event> getUserEvents(long userId, int from, int size) {
        return eventRepository.findByInitiator_idOrderById(userId, PageRequest.of(from / size, size));
    }

    public ewm.main.service.event.model.Event createEvent(NewEventDto newEventDto, long userId) {
        //дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации. (Ожидается код ошибки 409)
        if (LocalDateTime.parse(newEventDto.getEventDate(), EwmConstants.DATE_TIME_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
        }

        ewm.main.service.event.model.Event event = ewm.main.service.event.model.Event.builder()
                .category(categoryRepository.getReferenceById(newEventDto.getCategory()))
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), EwmConstants.DATE_TIME_FORMATTER))
                .location(newEventDto.getLocation())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .initiator(userService.getUserById(userId))
                .state(Event.PENDING)
                .build();

        return eventRepository.save(event);
    }

    public ewm.main.service.event.model.Event getEventByIdAndInitiatorId(long eventId, long initiatorId) {
        Optional<ewm.main.service.event.model.Event> optionalEvent = eventRepository.findByIdAndInitiator_id(eventId, initiatorId);
        if (optionalEvent.isEmpty()) {
            throw new EventNotFoundException("Событие " + eventId + " не найдено или недоступно");
        } else {
            return optionalEvent.get();
        }
    }

    public ewm.main.service.event.model.Event updateEventByInitiator(UpdateEventUserRequest eventRequest, long eventId, long initiatorId) {
        ewm.main.service.event.model.Event storageEvent = getEventByIdAndInitiatorId(eventId, initiatorId);

        //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
        if (storageEvent.getState() == Event.PUBLISHED) {
            throw new EventUpdateException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

        //поиск и проверка изменяемых полей
        if (eventRequest.getAnnotation() != null) {
            storageEvent.setAnnotation(eventRequest.getAnnotation());
        }

        if (eventRequest.getCategory() != null) {
            storageEvent.setCategory(categoryRepository.getReferenceById(eventRequest.getCategory()));
        }

        if (eventRequest.getDescription() != null) {
            storageEvent.setDescription(eventRequest.getDescription());
        }

        if (eventRequest.getEventDate() != null) {
            LocalDateTime eventRequestDate = LocalDateTime.parse(eventRequest.getEventDate(), EwmConstants.DATE_TIME_FORMATTER);

            //дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации. (Ожидается код ошибки 409)
            if (eventRequestDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EventUpdateException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
            }

            storageEvent.setEventDate(eventRequestDate);
        }

        if (eventRequest.getLocation() != null) {
            storageEvent.setLocation(eventRequest.getLocation());
        }

        if (eventRequest.getPaid() != null) {
            storageEvent.setPaid(eventRequest.getPaid());
        }

        if (eventRequest.getParticipantLimit() != null) {
            storageEvent.setParticipantLimit(eventRequest.getParticipantLimit());
        }

        if (eventRequest.getRequestModeration() != null) {
            storageEvent.setRequestModeration(eventRequest.getRequestModeration());
        }

        if (eventRequest.getTitle() != null) {
            storageEvent.setTitle(eventRequest.getTitle());
        }

        if (eventRequest.getStateAction() != null) {
            switch (eventRequest.getStateAction()) {
                case "SEND_TO_REVIEW":
                    storageEvent.setState(Event.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    storageEvent.setState(Event.CANCELED);
                    break;
            }
        }

        return eventRepository.save(storageEvent);
    }
}