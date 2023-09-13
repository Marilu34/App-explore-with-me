package ewm.main.service.compilation.model.dto;

import ewm.main.service.compilation.model.Compilation;
import ewm.main.service.event.model.dto.EventDtoMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompilationDtoMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation != null) {
            return CompilationDto.builder()
                    .id(compilation.getId())
                    .events(EventDtoMapper.toEventShortDtoList(compilation.getEvents()))
                    .pinned(compilation.getPinned())
                    .title(compilation.getTitle())
                    .build();
        } else {
            return null;
        }
    }

    public static List<CompilationDto> toCompilationDtoList(Collection<Compilation> compilations) {
        if (compilations != null) {
            List<CompilationDto> compilationDtoList = new ArrayList<>();
            for (Compilation compilation : compilations) {
                compilationDtoList.add(toCompilationDto(compilation));
            }
            return compilationDtoList;
        } else {
            return null;
        }
    }
}