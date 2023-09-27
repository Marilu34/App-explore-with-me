package ru.practicum.services;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventDtoRequest;
import ru.practicum.model.AllUserRequestFormat;
import ru.practicum.model.AllUserRequestResponse;
import ru.practicum.dto.UserRequestDto;
import ru.practicum.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    UserDto createUser(UserDto user);

    UserDto updateUser(Integer id, UserDto user);

    UserDto deleteUser(Integer id);

    EventDto createEventForUser(Integer userId, EventDtoRequest event);

    List<EventDto> getEventsForUser(Integer userId, Integer from, Integer size);

    EventDto getEventForUser(Integer userId, Integer eventId);

    EventDto updateEventForOwner(Integer userId, Integer eventId, EventDtoRequest event);

    UserRequestDto createRequestForEvent(Integer userId, Integer eventId);

    List<UserRequestDto> getRequestsForUser(Integer userId);

    UserRequestDto cancelUserRequestForEvent(Integer userId, Integer requestId);

    List<UserRequestDto> getAllRequestsForOwnerEvent(Integer userId, Integer eventId);

    AllUserRequestResponse changeStatusRequestForEvent(Integer userId, Integer eventId, AllUserRequestFormat request);
}