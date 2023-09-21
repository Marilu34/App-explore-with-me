package ewm.main.service.category.service;

import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.category.model.dto.ShortCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(ShortCategoryDto shortCategoryDto);

    CategoryDto updateCategory(Long categoryId, ShortCategoryDto shortCategoryDto);

    CategoryDto getCategoryById(Long categoryId);

    List<CategoryDto> getAllCategories(int from, int size);

    void deleteCategory(Long categoryId);
}