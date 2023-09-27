package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.UserRequest;
import ru.practicum.dto.UserRequestDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserRequestMapper {

    public static UserRequestDto toRequestDto(UserRequest userRequest) {
        return new UserRequestDto(userRequest.getId(),
                userRequest.getCreated(),
                userRequest.getEvent().getId(),
                userRequest.getRequester().getId(),
                userRequest.getStatus());
    }

    public static List<UserRequestDto> toRequestDtoList(List<UserRequest> list) {
        List<UserRequestDto> userRequestDtoList = new ArrayList<>();
        for (UserRequest request : list) {
            userRequestDtoList.add(toRequestDto(request));
        }
        return userRequestDtoList;
    }
}