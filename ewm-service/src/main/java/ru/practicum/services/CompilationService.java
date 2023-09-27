package ru.practicum.services;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationDtoRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(CompilationDtoRequest compilationDto);

    void deleteCompilation(Integer compId);

    CompilationDto updateCompilation(Integer compId, CompilationDtoRequest compilationDto);

    List<CompilationDto> getAllCompilation(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Integer id);
}
