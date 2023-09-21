package ewm.main.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.user.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public final class EventShortDto {
    private final Long id;
    private CategoryDto category;
    private final Long confirmedRequests;
    private final String annotation;
    private final UserShortDto initiator;
    private final Boolean paid;
    private final String title;
    private final Long views;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
}