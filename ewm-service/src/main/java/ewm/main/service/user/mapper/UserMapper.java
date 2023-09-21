package ewm.main.service.user.mapper;

import ewm.main.service.user.dto.NewUserRequest;
import ewm.main.service.user.dto.UserDto;
import ewm.main.service.user.dto.UserShortDto;
import ewm.main.service.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public User toUser(NewUserRequest newUserRequest) {
        return User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}