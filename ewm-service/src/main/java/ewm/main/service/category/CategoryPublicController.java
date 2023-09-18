package ewm.main.service.category;

import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.category.model.dto.CategoryDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@Positive @PathVariable long categoryId) {
        log.info("Получение публичной категории: categoryId = {}", categoryId);
        return CategoryDtoMapper.toCategoryDto(categoryService.getCategoryById(categoryId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                              @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получение всех публичных категорий: from = {}, size = {}", from, size);
        return CategoryDtoMapper.toCategoryDtoList(categoryService.getAllCategories(from, size));
    }


}
