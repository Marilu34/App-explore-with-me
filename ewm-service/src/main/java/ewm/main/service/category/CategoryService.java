package ewm.main.service.category;

import ewm.main.service.category.model.Category;
import ewm.main.service.event.EventService;
import ewm.main.service.exceptions.CategoryException;
import ewm.main.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final EventService eventService;

    private final CategoryRepository categoryRepository;


    public Category create(Category category) {
        Category storageCategory;
        try {
            storageCategory = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryException("Такая категория уже существует");
        }

        return storageCategory;
    }

    public Category update(Category category, long categoryId) {
        Category storageCategory;

        getCategoryById(categoryId);
        category.setId(categoryId);

        try {
            storageCategory = categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new CategoryException("Такая категория уже существует");
        }

        return storageCategory;
    }

    public Category getCategoryById(long categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new NotFoundException("Категория " + categoryId + " не обнаружена");
        } else {
            return optionalCategory.get();
        }
    }

    public List<Category> getAllCategories(int from, int size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).getContent();
    }

    public void delete(long categoryId) {
        Category category = getCategoryById(categoryId);
        if (eventService.getEventsCountByCategoryId(categoryId) > 0) {
            throw new CategoryException("С категорией " + categoryId + " связаны события");
        } else {
            categoryRepository.delete(category);
        }
    }
}