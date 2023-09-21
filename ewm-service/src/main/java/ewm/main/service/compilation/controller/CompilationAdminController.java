package ewm.main.service.compilation.controller;

import ewm.main.service.compilation.dto.CompilationDto;
import ewm.main.service.compilation.dto.NewCompilationDto;
import ewm.main.service.compilation.service.CompilationService;
import ewm.main.service.validator.ValidationGroups;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Validated
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(ValidationGroups.Create.class)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Создано компилирование " + newCompilationDto);
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated(ValidationGroups.Update.class)
    public CompilationDto updateCompilation(@PathVariable(name = "compId") Long compilationId, @Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("Обновлено компилирование " + newCompilationDto);
        return compilationService.updateCompilation(compilationId, newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(name = "compId") Long compilationId) {
        log.info("Удалено компилирование " + compilationId);
        compilationService.deleteCompilation(compilationId);
    }


}