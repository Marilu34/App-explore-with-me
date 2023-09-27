package ru.practicum.services;

import ru.practicum.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto deleteCategory(Integer id);

    CategoryDto updateCategory(Integer id, CategoryDto categoryDto);

    List<CategoryDto> getAllCategory(Integer from, Integer size);

    CategoryDto getCategory(Integer id);
}