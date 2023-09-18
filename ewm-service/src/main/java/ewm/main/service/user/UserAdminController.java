package ewm.main.service.user;

import ewm.main.service.user.model.dto.UserDto;
import ewm.main.service.user.model.dto.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserService userService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Администратор создал пользователя: {}", userDto);
        return UserDtoMapper.toUserDto(userService.create(UserDtoMapper.toUser(userDto)));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(@RequestParam(name = "ids", required = false) List<Long> userIdList,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                     @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Администратор получил доступ ко всем пользователям: ids = {}, from = {}, size = {}", userIdList, from, size);
        return UserDtoMapper.toUserDtoList(userService.getAllUsers(userIdList, from, size));
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable long userId) {
        log.info("Администратор удалил пользователя: userId = {}", userId);
        userService.delete(userId);
    }
}