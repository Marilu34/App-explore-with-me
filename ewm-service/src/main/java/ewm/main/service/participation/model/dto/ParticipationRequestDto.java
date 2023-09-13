package ewm.main.service.participation.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipationRequestDto {
    private long id;

    private String created;

    private long event;

    private long requester;

    private String status;
}