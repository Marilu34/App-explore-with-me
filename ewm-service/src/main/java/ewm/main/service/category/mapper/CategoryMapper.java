package ewm.main.service.category.mapper;

import ewm.main.service.category.model.Category;
import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.category.model.dto.ShortCategoryDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(ShortCategoryDto shortCategoryDto) {
        return Category.builder()
                .name(shortCategoryDto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public Category updateCategory(ShortCategoryDto shortCategoryDto, Long categoryId) {
        return Category.builder()
                .id(categoryId)
                .name(shortCategoryDto.getName())
                .build();
    }
}