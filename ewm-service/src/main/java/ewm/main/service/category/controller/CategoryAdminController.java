package ewm.main.service.category.controller;

import ewm.main.service.category.service.CategoryService;
import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.category.model.dto.ShortCategoryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid ShortCategoryDto shortCategoryDto) {
        log.info("Создана категория " + shortCategoryDto);
        return categoryService.createCategory(shortCategoryDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable("catId") Long categoryId, @RequestBody @Valid ShortCategoryDto shortCategoryDto) {
        log.info("Обновлена категория " + shortCategoryDto + " с id = " + categoryId);
        return categoryService.updateCategory(categoryId, shortCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long categoryId) {
        log.info("Удалена категория с id = " + categoryId);
        categoryService.deleteCategory(categoryId);
    }


}