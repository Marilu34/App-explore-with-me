package ewm.main.service.compilation.model.dto;

import ewm.main.service.event.model.dto.EventShortDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@Builder
public class CompilationDto {
    @PositiveOrZero
    private long id;

    private List<EventShortDto> events;

    @NotNull
    private Boolean pinned;

    @NotEmpty
    private String title;
}
