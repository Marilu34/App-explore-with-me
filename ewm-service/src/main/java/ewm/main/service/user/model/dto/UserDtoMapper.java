package ewm.main.service.user.model.dto;

import ewm.main.service.user.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDtoMapper {
    public static UserDto toUserDto(User user) {
        if (user != null) {
            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();
        } else {
            return null;
        }
    }

    public static UserShortDto toUserShortDto(User user) {
        if (user != null) {
            return UserShortDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .build();
        } else {
            return null;
        }
    }

    public static List<UserDto> toUserDtoList(List<User> userList) {
        if (userList != null) {
            return userList.stream().map(UserDtoMapper::toUserDto).collect(Collectors.toList());
        } else {
            return List.of();
        }
    }

    public static User toUser(UserDto userDto) {
        if (userDto != null) {
            return User.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .email(userDto.getEmail())
                    .build();
        } else {
            return null;
        }
    }

    public static User toUser(UserShortDto userShortDto) {
        if (userShortDto != null) {
            return User.builder()
                    .id(userShortDto.getId())
                    .name(userShortDto.getName())
                    .build();
        } else {
            return null;
        }
    }
}
