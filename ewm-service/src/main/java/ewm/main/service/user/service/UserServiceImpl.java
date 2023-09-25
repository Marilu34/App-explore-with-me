package ewm.main.service.user.service;

import ewm.main.service.exception.NotFoundException;
import ewm.main.service.exception.EmailExistsEmail;
import ewm.main.service.user.criteria.UserCriteria;
import ewm.main.service.user.dto.NewUserRequest;
import ewm.main.service.user.dto.UserDto;
import ewm.main.service.user.mapper.UserMapper;
import ewm.main.service.user.model.User;
import ewm.main.service.user.repository.UserRepository;
import ewm.main.service.user.repository.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(@NotNull NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new EmailExistsEmail("Email exists");
        }
        User user = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        UserCriteria criteria = UserCriteria.builder()
                .ids(ids)
                .build();
        UserSpecification userSpecification = new UserSpecification(criteria);
        return userRepository.findAll(userSpecification, pageable).stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(@NotNull Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not exists"));
        userRepository.deleteById(userId);
    }
}