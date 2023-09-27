package ru.practicum.services;

import ru.practicum.dto.*;
import ru.practicum.model.UsersRequest;
import ru.practicum.model.UserRequestResponse;

import java.util.List;

public interface UserService {


    UserDto createUser(UserDto user);

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

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

    UserRequestResponse changeStatusRequestForEvent(Integer userId, Integer eventId, UsersRequest request);

    CommentaryDto createComment(Integer userId, Integer eventId, CommentaryDto commentaryDto);

    CommentaryDto updateComment(Integer userId, Integer commentId, CommentaryDto commentaryDto);

    void deleteComment(Integer userId, Integer commentId);
}