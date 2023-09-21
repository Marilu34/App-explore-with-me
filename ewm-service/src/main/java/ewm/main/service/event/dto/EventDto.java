package ewm.main.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.event.model.EventState;
import ewm.main.service.event.model.Location;
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
public final class EventDto {
    private final Long id;
    private final String description;
    private final String annotation;
    private final CategoryDto category;
    private final Long views;
    private final String title;
    private final Long confirmedRequests;
    private final UserShortDto initiator;
    private final Location location;
    private final Boolean paid;
    private final Integer participantLimit;
    private final Boolean requestModeration;
    private final EventState state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime publishedOn;

}