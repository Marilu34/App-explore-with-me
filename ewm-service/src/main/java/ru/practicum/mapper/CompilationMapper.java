package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Compilation;
import ru.practicum.dto.CompilationDto;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return new CompilationDto(compilation.getId(), compilation.getEvents(),
                compilation.isPinned(), compilation.getTitle());
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations) {
        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation compilation : compilations) {
            compilationDtoList.add(toCompilationDto(compilation));
        }
        return compilationDtoList;
    }
}