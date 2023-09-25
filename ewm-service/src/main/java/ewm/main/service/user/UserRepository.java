package ewm.main.service.user;

import ewm.main.service.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIdInOrderById(List<Long> userIdList, Pageable page);
}