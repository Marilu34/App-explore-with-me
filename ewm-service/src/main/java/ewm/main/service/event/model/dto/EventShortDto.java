package ewm.main.service.event.model.dto;

import ewm.main.service.category.model.dto.CategoryDto;
import ewm.main.service.user.model.dto.UserShortDto;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class EventShortDto {
    private long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private int views;
}