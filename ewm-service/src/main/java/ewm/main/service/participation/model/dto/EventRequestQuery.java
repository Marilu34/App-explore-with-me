package ewm.main.service.participation.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class EventRequestQuery {
    @NotEmpty
    private List<Long> requestIds;

    @NotEmpty
    private String status;
}