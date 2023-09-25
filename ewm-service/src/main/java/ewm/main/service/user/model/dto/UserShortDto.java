package ewm.main.service.user.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@Builder
public class UserShortDto {
    @Positive
    private long id;

    @NotEmpty
    private String name;
}