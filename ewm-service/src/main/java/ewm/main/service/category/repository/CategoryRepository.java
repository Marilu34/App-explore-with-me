package ewm.main.service.category.repository;

import ewm.main.service.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Boolean existsByNameAndIdNot(String name, Long categoryId);

    Boolean existsByName(String name);
}