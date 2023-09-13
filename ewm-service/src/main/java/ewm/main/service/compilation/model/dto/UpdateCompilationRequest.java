package ewm.main.service.compilation.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events;

    private Boolean pinned;

    private String title;
}
