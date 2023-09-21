package ewm.main.service.user.service;

import ewm.main.service.user.dto.NewUserRequest;
import ewm.main.service.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}