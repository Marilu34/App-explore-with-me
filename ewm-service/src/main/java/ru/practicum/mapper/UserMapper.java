package ru.practicum.mapper;

import ru.practicum.model.User;
import ru.practicum.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto user) {
        User newUser = new User();
        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        return newUser;
    }

    public static List<UserDto> toUserDtoList(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        for (User user : users) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }
}