package ewm.main.service.event.model.dto;

import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.common.models.Location;
import ewm.main.service.user.model.dto.UserShortDto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class EventFullDto {
    private long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private int participantLimit;

    private String publishedOn;

    private boolean requestModeration;

    private String state;

    private String title;

    private int views;
}