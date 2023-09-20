package ewm.main.service.user;


import ewm.main.service.error_handler.AlreadyExistsException;
import ewm.main.service.error_handler.NotFoundException;
import ewm.main.service.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        User storageUser;
        try {
            storageUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("E-mail уже существует");
        }

        return storageUser;
    }

    public User getUserById(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        } else {
            return optionalUser.get();
        }
    }

    public List<User> getAllUsers(List<Long> userIdList, int from, int size) {
        if (userIdList == null || userIdList.isEmpty()) {
            return userRepository.findAll(PageRequest.of(from / size, size)).getContent();
        } else {
            return userRepository.findByIdInOrderById(userIdList, PageRequest.of(from / size, size));
        }
    }


    public void delete(long userId) {
        userRepository.delete(getUserById(userId));
    }
}
