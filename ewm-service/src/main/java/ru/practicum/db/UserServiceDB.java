package ru.practicum.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.dto.UserRequestDto;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventDtoRequest;
import ru.practicum.model.State;
import ru.practicum.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ResourceNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.UserRequestMapper;
import ru.practicum.model.AllUserRequestFormat;
import ru.practicum.model.AllUserRequestResponse;
import ru.practicum.model.Status;
import ru.practicum.model.UserRequest;
import ru.practicum.repository.UserRequestRepository;
import ru.practicum.mapper.UserMapper;
import ru.practicum.services.UserService;
import ru.practicum.model.User;
import ru.practicum.dto.UserDto;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceDB implements UserService {
    public static final int MAX_LENGTH_DESCRIPTION = 7000;
    public static final int MIN_LENGTH_DESCRIPTION = 20;
    public static final int MAX_LENGTH_ANNOTATION = 2000;
    public static final int MIN_LENGTH_ANNOTATION = 20;
    public static final int MAX_LENGTH_TITLE = 120;
    public static final int MIN_LENGTH_TITLE = 3;
    private final UserRepository repository;

    private final EventRepository eventRepository;

    private final CategoryRepository categoryRepository;

    private final UserRequestRepository requestRepository;

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        List<UserDto> users;
        if (ids.size() == 1 && ids.get(0) == 0) {
            users = UserMapper.toUserDtoList(repository.findAll(PageRequest.of(from, size)).toList());
        } else {
            users = UserMapper.toUserDtoList(repository.findByIdIn(ids, PageRequest.of(from, size)).toList());
        }
        return users;
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("Нужно заполнить имя!");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Почта некорректна");
        }
        if (user.getName().length() < 2 || user.getName().length() > 250) {
            throw new ValidationException("Имя слишком длинное или короткое!");
        }
        if (user.getEmail().length() < 6 || user.getEmail().length() > 254 || !isValidEmail(user.getEmail())) {
            throw new ValidationException("Почта слишком длинная или короткая!");
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(user)));
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer id, UserDto user) {
        user.setId(id);
        User existsUser = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Такого пользователя не существует"));
        if (user.getName() != null) {
            existsUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existsUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(repository.save(existsUser));
    }

    @Override
    @Transactional
    public UserDto deleteUser(Integer id) {
        UserDto user = UserMapper.toUserDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя нет!")));
        repository.deleteById(id);
        return user;
    }

    @Override
    @Transactional
    public EventDto createEventForUser(Integer userId, EventDtoRequest event) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        if (event.getDescription() == null || event.getDescription().trim().isEmpty()
                || event.getDescription().length() < MIN_LENGTH_DESCRIPTION
                || event.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationException("Описание не должно быть пустым или быть меньше 20 символов!");
        }
        if (event.getAnnotation() == null || event.getAnnotation().trim().isEmpty()
                || event.getAnnotation().length() < MIN_LENGTH_ANNOTATION
                || event.getAnnotation().length() > MAX_LENGTH_ANNOTATION) {
            throw new ValidationException("Аннотация не должна быть: " + "\n" +
                    "- пустой " + "\n" +
                    "- меньше 20 символов или больше 2000");
        }
        if (event.getTitle().length() < MIN_LENGTH_TITLE || event.getTitle().length() > MAX_LENGTH_TITLE) {
            throw new ValidationException("Не валидная длина заголовка!");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Невозможно установить такое время!");
        }
        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        Event newEvent = EventMapper.toEvent(event, repository.findById(userId).get());
        newEvent.setCategory(categoryRepository.findById(newEvent.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Такой категории не существует!")));
        newEvent.setState(State.PENDING);
        newEvent.setViews(0);
        newEvent.setConfirmedRequests(0);
        return EventMapper.toEventDto(eventRepository.save(newEvent));
    }

    @Override
    public List<EventDto> getEventsForUser(Integer userId, Integer from, Integer size) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        return EventMapper.toEventDtoList(eventRepository.findByInitiator(repository.findById(userId).get(),
                PageRequest.of(from / size, size)).toList());
    }

    @Override
    public EventDto getEventForUser(Integer userId, Integer eventId) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        if (eventRepository.findByInitiatorAndId(repository.findById(userId).get(), eventId).isEmpty()) {
            throw new ResourceNotFoundException("Данный пользователь не создатель события");
        }
        return EventMapper.toEventDto(eventRepository.findByInitiatorAndId(repository
                .findById(userId).get(), eventId).get());
    }

    @Override
    @Transactional
    public EventDto updateEventForOwner(Integer userId, Integer eventId, EventDtoRequest event) {
        if (repository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("Такого пользователя не существует");
        }
        if (eventRepository.findByInitiatorAndId(repository.findById(userId).get(), eventId).isEmpty()) {
            throw new ConflictException("Данный пользователь не создатель события");
        }
        Event newEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого события нет"));
        if (newEvent.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие уже опубликовано!");
        }
        EventDto eventResponse;
        if (event.getAnnotation() != null) {
            if (event.getAnnotation() == null || event.getAnnotation().trim().isEmpty()
                    || event.getAnnotation().length() < MIN_LENGTH_ANNOTATION
                    || event.getAnnotation().length() > MAX_LENGTH_ANNOTATION) {
                throw new ValidationException("Аннотация не должна быть: \n" +
                        "- пустой \n" +
                        "- меньше 20 символов или больше 2000");
            }
            newEvent.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            newEvent.setCategory(categoryRepository.findById(event.getCategory())
                    .orElseThrow(() -> new ResourceNotFoundException("Такой категории не существует")));
        }
        if (event.getDescription() != null) {
            if (event.getDescription() == null || event.getDescription().trim().isEmpty()
                    || event.getDescription().length() < MIN_LENGTH_DESCRIPTION
                    || event.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
                throw new ValidationException("Описание не должно быть пустым или быть меньше 20 или " +
                        "быть больше 7000 символов!");
            }
            newEvent.setDescription(event.getDescription());
        }
        if (event.getEventDate() != null) {
            if (event.getEventDate().isBefore(newEvent.getEventDate()) ||
                    event.getEventDate().isBefore(LocalDateTime.now())) {
                throw new ValidationException("Невозможно установить такую дату");
            }
            newEvent.setEventDate(event.getEventDate());
        }
        if (event.getLocation() != null) {
            newEvent.setLat(event.getLocation().getLat());
            newEvent.setLon(event.getLocation().getLon());
        }
        if (event.getPaid() != null) {
            newEvent.setPaid(event.getPaid());
        }
        if (event.getParticipantLimit() != null) {
            newEvent.setParticipantLimit(event.getParticipantLimit());
        }
        if (event.getRequestModeration() != null) {
            newEvent.setRequestModeration(event.getRequestModeration());
        }
        if (event.getTitle() != null) {
            if (event.getTitle().length() < MIN_LENGTH_TITLE || event.getTitle().length() > MAX_LENGTH_TITLE) {
                throw new ValidationException("Не валидная длина заголовка!");
            }
            newEvent.setTitle(event.getTitle());
        }
        if (event.getStateAction() != null) {
            if (event.getStateAction().equals("SEND_TO_REVIEW")) {
                if (newEvent.getState().equals(State.PUBLISHED)) {
                    throw new ConflictException("Событие уже опубликовано или отменено!");
                }
                newEvent.setState(State.PENDING);
            } else if (event.getStateAction().equals("CANCEL_REVIEW")) {
                if (newEvent.getState().equals(State.PUBLISHED)) {
                    throw new ConflictException("Событие уже нельзя отклонить!");
                }
                newEvent.setState(State.CANCELED);
            }
        }
        return eventResponse = EventMapper.toEventDto(eventRepository.save(newEvent));
    }

    @Override
    public List<UserRequestDto> getAllRequestsForOwnerEvent(Integer userId, Integer eventId) {
        if (!eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"))
                .getInitiator().getId().equals(userId)) {
            throw new ValidationException("Посмотреть все запросы может только его создатель!");
        }
        return UserRequestMapper.toRequestDtoList(
                requestRepository.findAllByEvent(
                        eventRepository.findById(eventId)
                                .orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"))));
    }

    @Override
    @Transactional
    public AllUserRequestResponse changeStatusRequestForEvent(Integer userId, Integer eventId,
                                                              AllUserRequestFormat request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Изменять запросы может только его создатель!");
        }
        if (!(event.getConfirmedRequests() < event.getParticipantLimit())) {
            throw new ConflictException("Количество заявок максимально!");
        }
        User requester = null;
        for (Integer id : request.getRequestIds()) {
            UserRequest userRequest = requestRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Запроса с id " + id + "нет!"));
            if (!userRequest.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Заявка уже принята или отклонена!");
            }
            userRequest.setStatus(request.getStatus());
            requester = userRequest.getRequester();
            requestRepository.save(userRequest);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        eventRepository.save(event);
        return new AllUserRequestResponse(
                UserRequestMapper.toRequestDtoList(requestRepository.findAllByStatusAndRequester(Status.CONFIRMED,
                        requester)),
                UserRequestMapper.toRequestDtoList(requestRepository.findAllByStatusAndRequester(Status.REJECTED,
                        requester)));
    }

    @Override
    @Transactional
    public UserRequestDto createRequestForEvent(Integer userId, Integer eventId) {
        UserRequest userRequest = new UserRequest();
        for (UserRequest request : requestRepository.findAll()) {
            if (request.getEvent().getId().equals(eventId) && request.getRequester().getId().equals(userId)) {
                throw new ConflictException("Нельзя делать повторный запрос!");
            }
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException("Такого события нет!"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может делать запрос на свое событие!");
        }
        if (event.getPublishedOn() == null) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии!");
        }
        if (event.getParticipantLimit() == 0) {
            userRequest.setCreated(LocalDateTime.now());
            userRequest.setEvent(event);
            userRequest.setRequester(repository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует!")));
            userRequest.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else if (!event.isRequestModeration()) {
            if (!(event.getConfirmedRequests() < event.getParticipantLimit()) ||
                    event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                throw new ConflictException("Достигнут лимит запросов на участие!");
            }
            userRequest.setCreated(LocalDateTime.now());
            userRequest.setEvent(event);
            userRequest.setRequester(repository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует!")));
            userRequest.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            if (!(event.getConfirmedRequests() < event.getParticipantLimit()) ||
                    event.getParticipantLimit().equals(event.getConfirmedRequests())) {
                throw new ConflictException("Достигнут лимит запросов на участие!");
            }
            userRequest.setCreated(LocalDateTime.now());
            userRequest.setEvent(event);
            userRequest.setRequester(repository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует!")));
            userRequest.setStatus(Status.PENDING);
            eventRepository.save(event);
        }
        return UserRequestMapper.toRequestDto(requestRepository.save(userRequest));
    }

    @Override
    public List<UserRequestDto> getRequestsForUser(Integer userId) {
        return UserRequestMapper.toRequestDtoList(
                requestRepository.findAllByRequester(
                        repository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Такого пользователя не существует!"))));
    }

    @Override
    @Transactional
    public UserRequestDto cancelUserRequestForEvent(Integer userId, Integer requestId) {
        if (!requestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Такого запроса нет!"))
                .getRequester().getId().equals(userId)) {
            throw new ConflictException("Отменить запрос может только его создатель!");
        }
        UserRequest userRequest = requestRepository
                .findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Такого события нет!"));
        userRequest.setStatus(Status.CANCELED);
        return UserRequestMapper.toRequestDto(userRequest);
    }

    private boolean isValidEmail(String email) {
        String[] parts = email.split("@");

        String localPart = parts[0];
        String domainPart = parts[1];

        if (localPart.length() > 64) {
            return false;
        }

        String[] domainParts = domainPart.split("\\.");
        for (String domain : domainParts) {
            if (domain.length() > 63) {
                return false;
            }
        }
        return true;
    }
}