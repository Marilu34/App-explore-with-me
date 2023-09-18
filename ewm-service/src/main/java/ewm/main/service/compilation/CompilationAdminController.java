package ewm.main.service.compilation;

import ewm.main.service.compilation.model.dto.CompilationDto;
import ewm.main.service.compilation.model.dto.CompilationDtoMapper;
import ewm.main.service.compilation.model.dto.ShortCompilationDto;
import ewm.main.service.compilation.model.dto.CompilationRequest;
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
    public CompilationDto create(@Valid @RequestBody ShortCompilationDto shortCompilationDto) {
        log.info("Администратор создал компиляцию: {}", shortCompilationDto);
        return CompilationDtoMapper.toCompilationDto(compilationService.create(shortCompilationDto));
    }

    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@RequestBody CompilationRequest compilationRequest,
                                 @Positive @PathVariable long compilationId) {
        log.info("Администратор обновил компиляцию: {}, compilationId = {}", compilationRequest, compilationId);
        return CompilationDtoMapper.toCompilationDto(compilationService.update(compilationRequest, compilationId));
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable long compilationId) {
        log.info("Администратор удалил компиляцию: compilationId = {}", compilationId);
        compilationService.delete(compilationId);
    }
}
