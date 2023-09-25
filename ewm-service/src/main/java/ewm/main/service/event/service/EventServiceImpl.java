package ewm.main.service.event.service;

import ewm.main.service.category.repository.CategoryRepository;
import ewm.main.service.category.model.Category;
import ewm.main.service.event.standart.EventStandart;
import ewm.main.service.event.dto.EventDto;
import ewm.main.service.event.dto.EventShortDto;
import ewm.main.service.event.dto.NewEventDto;
import ewm.main.service.event.dto.UpdateEventDto;
import ewm.main.service.event.mapper.EventMapper;
import ewm.main.service.event.model.*;
import ewm.main.service.event.repository.EventRepository;
import ewm.main.service.event.repository.EventBase;
import ewm.main.service.exception.NotFoundException;
import ewm.main.service.exception.EventWrongTimeException;
import ewm.main.service.exception.WrongEventStateException;
import ewm.main.service.user.model.User;
import ewm.main.service.user.repository.UserRepository;
import ewm.stats.client.Client;
import ewm.stats.dto.EndpointDto;
import ewm.stats.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final CategoryRepository categoryRepository;
    private final Client client;
    private final String appName = "EwmService";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public EventDto createEvent(@NotNull Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь не существует"));
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() -> new NotFoundException("Категория не существует"));
        Event event = EventMapper.toEvent(newEventDto, user, category);
        event.setState(EventState.PENDING);
        event.setConfirmedRequest(0L);
        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventDto updateEventAdmin(@NotNull Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ValidationException("Мероприятие не существует"));
        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.REJECT_EVENT)) {
            throw new WrongEventStateException("Ошибка! Мероприятие не обнаружено");
        }
        Category category = event.getCategory();
        if (updateEventDto.getCategory() != null) {
            category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(() -> new NotFoundException("Категория не существует"));
        }
        EventState state = event.getState();
        if (updateEventDto.getStateAction() != null) {
            AdminRequestState adminRequestState = AdminRequestState.valueOf(updateEventDto.getStateAction());
            if (adminRequestState == AdminRequestState.PUBLISH_EVENT) {
                state = EventState.PUBLISHED;
                event.setPublishedOn(LocalDateTime.now());
            } else {
                state = EventState.REJECT_EVENT;
            }
        }
        return EventMapper.toEventFullDto(eventRepository.save(EventMapper.updateEvent(updateEventDto, event, category, state)));
    }


    @Override
    @Transactional
    public EventDto updateEvent(@NotNull Long userId,@NotNull Long eventId, UpdateEventDto updateEventDto) {
        userRepository.findById(userId).orElseThrow(() -> new ValidationException("Пользователь не существует"));

        Event event = eventRepository.findAllByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не обнаружено"));

        if (!(event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.REJECT_EVENT))) {
            throw new WrongEventStateException("Ошибка! Мероприятие не обнаружено");
        }

        Category patchCategory = event.getCategory();
        if (updateEventDto.getCategory() != null) {
            patchCategory = categoryRepository.findById(updateEventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не существует"));
        }

        EventState state = event.getState();
        if (updateEventDto.getStateAction() != null) {
            UserRequestState updateState;
            try {
                updateState = UserRequestState.valueOf(updateEventDto.getStateAction());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Ошибка! Мероприятие не обнаружено");
            }

            switch (updateState) {
                case CANCEL_REVIEW:
                    state = EventState.CANCELED;
                    break;
                case SEND_TO_REVIEW:
                    state = EventState.PENDING;
                    break;
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(
                EventMapper.updateEvent(updateEventDto, event, patchCategory, state)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getAdminEvent(@NotNull Admin admin) {
        if (admin.getRangeStart() != null && admin.getRangeEnd() != null && admin.getRangeStart().isAfter(admin.getRangeEnd())) {
            throw new EventWrongTimeException("неправильное время мероприятия");
        }
        if (admin.getRangeStart() == null && admin.getRangeEnd() == null) {
            admin.setRangeStart(LocalDateTime.now());
        }
        Pageable pageable = PageRequest.of(admin.getFrom() / admin.getSize(), admin.getSize());

        List<EventState> convertStates = null;
        if (admin.getStates() != null) {
            convertStates = admin.getStates().stream()
                    .map(EventState::valueOf)
                    .collect(Collectors.toList());
        }

        EventStandart standart = EventStandart.builder()
                .users(admin.getUsers())
                .states(convertStates)
                .categories(admin.getCategories())
                .rangeStart(admin.getRangeStart())
                .rangeEnd(admin.getRangeEnd())
                .build();

        EventBase eventBase = new EventBase(standart);
        Page<Event> eventsPage = eventRepository.findAll(eventBase, pageable);

        Map<Long, Long> eventViews = getEventsViews(eventsPage.getContent());

        List<Event> eventsWithHits = eventsPage.getContent().stream()
                .peek(event -> event.setViews(eventViews.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        return eventsWithHits.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(@NotNull  Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не существует"));

        Pageable pageable = PageRequest.of(from / size, size);

        Page<Event> userEventsPage = eventRepository.findAllByInitiatorId(userId, pageable);
        List<Event> userEvents = userEventsPage.getContent();

        Map<Long, Long> eventViews = getEventsViews(userEvents);

        userEvents.forEach(event -> event.setViews(eventViews.getOrDefault(event.getId(), 0L)));

        return userEvents.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventById(@NotNull Long id, @NotNull String ip) {
        Event event = eventRepository.findPublishedEventById(id)
                .orElseThrow(() -> new NotFoundException("Опубликованные события не существуют"));

        client.saveEndpoint(EndpointDto.builder()
                .app(appName)
                .uri("/events/" + id)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build());

        Map<Long, Long> eventViews = getEventsViews(List.of(event));
        event.setViews(eventViews.getOrDefault(id, 0L));

        return EventMapper.toEventFullDto(event);
    }


    @Override
    @Transactional(readOnly = true)
    public EventDto getUserEventById(@NotNull Long userId, @NotNull Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не существует"));
        Event event = eventRepository.findAllByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Мероприятие не обнаружено"));

        Map<Long, Long> eventViews = getEventsViews(List.of(event));

        event.setViews(eventViews.getOrDefault(event.getId(), 0L));

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public List<EventDto> getEvents(@NotNull Public publicEvents) {
        if (publicEvents.getRangeStart() != null && publicEvents.getRangeEnd() != null && publicEvents.getRangeStart().isAfter(publicEvents.getRangeEnd())) {
            throw new EventWrongTimeException("Неправильное время события");
        }
        client.saveEndpoint(EndpointDto.builder()
                .app(appName)
                .uri("/events")
                .ip(publicEvents.getIp())
                .timestamp(LocalDateTime.now())
                .build());
        if (publicEvents.getRangeStart() == null && publicEvents.getRangeEnd() == null) {
            publicEvents.setRangeStart(LocalDateTime.now());
        }
        Pageable pageable = PageRequest.of(publicEvents.getFrom() / publicEvents.getSize(), publicEvents.getSize());
        EventStandart eventStandart = EventStandart.builder()
                .states(List.of(EventState.PUBLISHED))
                .text(publicEvents.getText())
                .categories(publicEvents.getCategories())
                .rangeEnd(publicEvents.getRangeEnd())
                .rangeStart(publicEvents.getRangeStart())
                .onlyAvailable(publicEvents.getOnlyAvailable())
                .sortParam(publicEvents.getSortParameter() != null ? SortParameter.valueOf(publicEvents.getSortParameter()) : null)
                .build();
        EventBase eventBase = new EventBase(eventStandart);

        Page<Event> eventsPage = eventRepository.findAll(eventBase, pageable);
        Map<Long, Long> eventViews = getEventsViews(eventsPage.getContent());

        List<Event> eventsWithHits = eventsPage.getContent().stream()
                .peek(event -> event.setViews(eventViews.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        return eventsWithHits.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    private Map<Long, Long> getEventsViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> uris = events.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .collect(Collectors.toList());

        List<ViewStatsDto> stats = getClientViewStats(uris);

        return stats.stream()
                .collect(Collectors.toMap(
                        viewStatsDto -> getEventIdFromUri(viewStatsDto.getUri()),
                        ViewStatsDto::getHits
                ));
    }

    private List<ViewStatsDto> getClientViewStats(List<String> uris) {
        try {
            return client.getViewStats(
                    LocalDateTime.now().minusMinutes(1),
                    LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1),
                    uris,
                    true
            );
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // извлечь идентификатор события из строки uri и
    // использовать его для дальнейшей обработки или использования в коде
    private Long getEventIdFromUri(String uri) {
        String[] uriElements = uri.split("/");
        return Long.parseLong(uriElements[uriElements.length - 1]);
    }
}