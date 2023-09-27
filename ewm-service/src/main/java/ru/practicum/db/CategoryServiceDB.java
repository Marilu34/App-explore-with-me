package ru.practicum.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.dto.CategoryDto;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ResourceNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.services.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceDB implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            throw new ValidationException("Имя не может быть пустым!");
        }
        if (categoryDto.getName().length() > 50) {
            throw new ValidationException("Имя слишком длинное!");
        }
        return CategoryMapper.toCategoryDto(repository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto deleteCategory(Integer id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Такой категории нет!");
        }
        if (!eventRepository.findByCategory(repository.findById(id).get()).isEmpty()) {
            throw new ConflictException("Категория связанна с событием!");
        }
        CategoryDto deleteCategory = CategoryMapper.toCategoryDto(repository.findById(id).get());
        repository.deleteById(id);
        return deleteCategory;
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Такой категории нет!");
        }
        if (categoryDto.getName().length() > 50) {
            throw new ValidationException("Имя слишком длинное!");
        }
        Category oldCategory = repository.findById(id).get();
        oldCategory.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(repository.save(oldCategory));
    }

    @Override
    public List<CategoryDto> getAllCategory(Integer from, Integer size) {
        return CategoryMapper.toCategoryDtoList(repository.findAll(PageRequest.of(from / size, size)).toList());
    }

    @Override
    public CategoryDto getCategory(Integer id) {
        if (repository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Такой категории нет!");
        }
        return CategoryMapper.toCategoryDto(repository.findById(id).get());
    }
}