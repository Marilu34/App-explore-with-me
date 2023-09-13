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
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Admin create category: {}", categoryDto);
        CategoryDto result = CategoryDtoMapper.toCategoryDto(categoryService.create(CategoryDtoMapper.toCategory(categoryDto)));
        log.info("Admin create category result: {}", result);
        return result;
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@Valid @RequestBody CategoryDto categoryDto, @Positive @PathVariable long categoryId) {
        log.info("Admin update category: {}, categoryId = {}", categoryDto, categoryId);
        return CategoryDtoMapper.toCategoryDto(categoryService.update(CategoryDtoMapper.toCategory(categoryDto), categoryId));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable long categoryId) {
        log.info("Admin delete category: categoryId = {}", categoryId);
        categoryService.delete(categoryId);
    }
}