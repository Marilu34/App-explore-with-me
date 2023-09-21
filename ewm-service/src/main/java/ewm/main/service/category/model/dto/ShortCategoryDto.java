package ewm.main.service.category.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(force = true)
public final class ShortCategoryDto {
    @NotBlank
    @Size(max = 50)
    private final String name;
}