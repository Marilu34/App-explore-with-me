package ewm.main.service.compilation.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
public class ShortCompilationDto {

    private Set<Long> events;

    private Boolean pinned;

    @NotEmpty
    private String title;
}