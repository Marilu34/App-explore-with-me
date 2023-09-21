package ewm.main.service.compilation.service;

import ewm.main.service.compilation.dto.CompilationDto;
import ewm.main.service.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Long compilationId, NewCompilationDto newCompilationDto);

    CompilationDto getCompilation(Long compilationId);

    List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size);

    void deleteCompilation(Long compilationId);
}