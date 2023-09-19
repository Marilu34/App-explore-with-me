package ewm.main.service.event;

import ewm.main.service.category.CategoryRepository;
import ewm.main.service.common.Date;
import ewm.main.service.common.models.State;
import ewm.main.service.event.model.Event;
import ewm.main.service.event.model.dto.EventDtoUpdated;
import ewm.main.service.event.model.dto.EventAdminRequest;
import ewm.main.service.event.model.dto.EventUserRequest;
import ewm.main.service.exceptions.NotFoundException;
import ewm.main.service.exceptions.EventException;
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
    private final UserService userService;
    private final StatClient statClient;
    private final EventRepository eventRepository;
    private final EventRepositoryImpl eventRepositoryImpl;
    private final CategoryRepository categoryRepository;

    public Event createEvent(EventDtoUpdated eventDtoUpdated, long userId) {
        if (LocalDateTime.parse(eventDtoUpdated.getEventDate(), Date.DATE_TIME_FORMATTER).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
        }

        Event event = Event.builder()
                .category(categoryRepository.getReferenceById(eventDtoUpdated.getCategory()))
                .annotation(eventDtoUpdated.getAnnotation())
                .description(eventDtoUpdated.getDescription())
                .eventDate(LocalDateTime.parse(eventDtoUpdated.getEventDate(), Date.DATE_TIME_FORMATTER))
                .location(eventDtoUpdated.getLocation())
                .paid(eventDtoUpdated.isPaid())
                .participantLimit(eventDtoUpdated.getParticipantLimit())
                .requestModeration(eventDtoUpdated.isRequestModeration())
                .title(eventDtoUpdated.getTitle())
                .createdOn(LocalDateTime.now())
                .publishedOn(LocalDateTime.now())
                .initiator(userService.getUserById(userId))
                .state(State.PENDING)
                .build();

        return eventRepository.save(event);
    }

    public Event updateEventByAdmin(EventAdminRequest updateRequest, long eventId) {
        Event storageEvent = getEventById(eventId);

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
        if (updateRequest.getStateAction() != null && "REJECT_EVENT".equals(updateRequest.getStateAction())) {
            if (storageEvent.getState() != State.PENDING) {
                throw new EventException("Не удается опубликовать событие, потому что оно находится в неправильном состоянии: " + storageEvent.getState().toString());
            } else {
                storageEvent.setState(State.CANCELED);
            }
        }

        if (updateRequest.getStateAction() != null && "PUBLISH_EVENT".equals(updateRequest.getStateAction())) {
            if (storageEvent.getState() != State.PENDING) {
                throw new EventException("Не удается опубликовать событие, потому что оно находится в неправильном состоянии: " + storageEvent.getState().toString());
            } else {
                storageEvent.setState(State.PUBLISHED);
                storageEvent.setPublishedOn(LocalDateTime.now());
            }
        }

        if (updateRequest.getEventDate() != null) {
            LocalDateTime newEventDate = LocalDateTime.parse(updateRequest.getEventDate(), Date.DATE_TIME_FORMATTER);
            if (newEventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new EventException("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
            } else {
                storageEvent.setEventDate(newEventDate);
            }
        }


        return eventRepository.save(storageEvent);
    }

    public List<Event> getUserEvents(long userId, int from, int size) {
        return eventRepository.findByInitiator_idOrderById(userId, PageRequest.of(from / size, size));
    }

    public Event getEventById(long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new NotFoundException("Событие " + eventId + " не найдено");
        } else {
            return optionalEvent.get();
        }
    }

    public Event getEventByIdPublic(long eventId, String requestUri, String remoteIp) {
        Event event = getEventById(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие " + eventId + " не найдено");
        }

        statClient.newStat(requestUri, remoteIp, LocalDateTime.now());
        return event;
    }

    public int getEventsCountByCategoryId(long categoryId) {
        return eventRepository.getEventsCountByCategoryId(categoryId);
    }

    public List<Event> getAllEventsAdmin(List<Integer> usersIdList,
                                         List<State> states,
                                         List<Integer> categoriesIdList,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size) {
        return eventRepositoryImpl.findAllEventsByFilterAdmin(usersIdList, states, categoriesIdList, rangeStart, rangeEnd, from, size);
    }

    public List<Event> getAllEventsPublic(String text,
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
        statClient.newStat(requestUri, remoteIp, LocalDateTime.now());

        return eventRepositoryImpl.findAllEventsByFilterPublic(text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }


    public Event getEventByIdAndInitiatorId(long eventId, long initiatorId) {
        Optional<Event> optionalEvent = eventRepository.findByIdAndInitiator_id(eventId, initiatorId);
        if (optionalEvent.isEmpty()) {
            throw new NotFoundException("Событие " + eventId + " не найдено или недоступно");
        } else {
            return optionalEvent.get();
        }
    }

    public Event updateEventByInitiator(EventUserRequest eventRequest, long eventId, long initiatorId) {
        Event storageEvent = getEventByIdAndInitiatorId(eventId, initiatorId);

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
                    storageEvent.setState(State.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    storageEvent.setState(State.CANCELED);
                    break;
            }
        }
        if (storageEvent.getState() == State.PUBLISHED) {
            throw new EventException("Изменить можно только отмененные события или события в состоянии ожидания модерации");
        }

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
            LocalDateTime eventRequestDate = LocalDateTime.parse(eventRequest.getEventDate(), Date.DATE_TIME_FORMATTER);

            if (eventRequestDate.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EventException("Дата начала изменяемого события должна быть не ранее чем за два часа от даты публикации");
            }

            storageEvent.setEventDate(eventRequestDate);
        }
        return eventRepository.save(storageEvent);
    }
}