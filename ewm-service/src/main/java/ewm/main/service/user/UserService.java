package ewm.main.service.user;

import ewm.main.service.common.pagination.PaginationCalculator;
import ewm.main.service.exceptions.UserEmailNotUniqueException;
import ewm.main.service.exceptions.UserNotFoundException;
import ewm.main.service.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Пользователь " + userId + " не найден");
        } else {
            return optionalUser.get();
        }
    }

    public List<User> getAllUsers(List<Long> userIdList, int from, int size) {
        Pageable page = PaginationCalculator.getPage(from, size);
        if (userIdList == null || userIdList.isEmpty()) {
            return userRepository.findAll(page).getContent();
        } else {
            return userRepository.findByIdInOrderById(userIdList, page);
        }
    }

    public User create(User user) {
        User storageUser;
        try {
            storageUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserEmailNotUniqueException("E-mail не уникален");
        }

        return storageUser;
    }

    public void delete(long userId) {
        userRepository.delete(getUserById(userId));
    }
}
