package ewm.main.service.compilation;

import ewm.main.service.compilation.model.dto.CompilationDto;
import ewm.main.service.compilation.model.dto.CompilationDtoMapper;
import ewm.main.service.compilation.model.dto.NewCompilationDto;
import ewm.main.service.compilation.model.dto.UpdateCompilationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Admin create compilation: {}", newCompilationDto);
        return CompilationDtoMapper.toCompilationDto(compilationService.create(newCompilationDto));
    }

    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@RequestBody UpdateCompilationRequest updateCompilationRequest,
                                 @Positive @PathVariable long compilationId) {
        log.info("Admin update compilation: {}, compilationId = {}", updateCompilationRequest, compilationId);
        return CompilationDtoMapper.toCompilationDto(compilationService.update(updateCompilationRequest, compilationId));
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable long compilationId) {
        log.info("Admin delete compilation: compilationId = {}", compilationId);
        compilationService.delete(compilationId);
    }
}
