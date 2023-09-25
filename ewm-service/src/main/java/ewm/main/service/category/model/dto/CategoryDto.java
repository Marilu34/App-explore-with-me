package ewm.main.service.category.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class CategoryDto {
    private long id;

    @NotEmpty
    private String name;
}