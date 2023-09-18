package ewm.main.service.category;

import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.category.model.dto.CategoryDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryAdminController {
    private final CategoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategoryDto(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Администратор создал категорию: {}", categoryDto);
        CategoryDto result = CategoryDtoMapper.toCategoryDto(service.create(CategoryDtoMapper.toCategory(categoryDto)));
        log.info("Администратор создает результат категории: {}", result);
        return result;
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategoryDto(@Valid @RequestBody CategoryDto categoryDto, @Positive @PathVariable long categoryId) {
        log.info("Администратор обновил категорию: {}, categoryId = {}", categoryDto, categoryId);
        return CategoryDtoMapper.toCategoryDto(service.update(CategoryDtoMapper.toCategory(categoryDto), categoryId));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryDto(@Positive @PathVariable long categoryId) {
        log.info("Администратор удалил категорию: categoryId = {}", categoryId);
        service.delete(categoryId);
    }
}