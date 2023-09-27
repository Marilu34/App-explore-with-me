package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Category;
import ru.practicum.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public static List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        List<CategoryDto> categoriesDto = new ArrayList<>();
        for (Category category : categories) {
            categoriesDto.add(toCategoryDto(category));
        }
        return categoriesDto;
    }
}