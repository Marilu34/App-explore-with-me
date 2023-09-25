package ewm.main.service.category.service;

import ewm.main.service.category.model.Category;
import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.category.mapper.CategoryMapper;
import ewm.main.service.category.model.dto.ShortCategoryDto;
import ewm.main.service.category.repository.CategoryRepository;
import ewm.main.service.event.repository.EventRepository;
import ewm.main.service.exception.NotFoundException;
import ewm.main.service.exception.CategoryDeleteException;
import ewm.main.service.exception.NameExistsException;
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
public class CategoryServiceImpl implements CategoryService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;


    @Override
    @Transactional
    public CategoryDto createCategory(ShortCategoryDto shortCategoryDto) {
        if (categoryRepository.existsByName(shortCategoryDto.getName())) {
            throw new NameExistsException("такое имя уже существует");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(shortCategoryDto)));
    }

    @Override
    @Transactional
    public void deleteCategory(@NotNull Long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not exists"));
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new CategoryDeleteException("Категория уже используется в мероприятиях");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(@NotNull Long categoryId, @NotNull ShortCategoryDto shortCategoryDto) {
        categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category not exists"));
        if (categoryRepository.existsByNameAndIdNot(shortCategoryDto.getName(), categoryId)) {
            throw new NameExistsException("такое имя уже существует");
        }
        return CategoryMapper.toCategoryDto(CategoryMapper.updateCategory(shortCategoryDto, categoryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageable).stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(@NotNull Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Категория не существует"));
        return CategoryMapper.toCategoryDto(category);
    }
}