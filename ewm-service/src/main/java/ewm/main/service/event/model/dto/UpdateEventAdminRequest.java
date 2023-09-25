package ewm.main.service.event.model.dto;

import ewm.main.service.common.models.Location;
import lombok.Data;
@Data
public class UpdateEventAdminRequest {
    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    private String title;
}