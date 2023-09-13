package ewm.main.service.user.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class UserDto {
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;
}
