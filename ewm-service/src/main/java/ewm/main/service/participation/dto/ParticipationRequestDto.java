package ewm.main.service.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.main.service.participation.model.ParticipationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public final class ParticipationRequestDto {
    private final Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private final LocalDateTime created;
    private final Long event;
    private final Long requester;
    private final ParticipationStatus status;
}