package ewm.main.service.event.model.dto;

import ewm.main.service.common.models.Location;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
public class NewEventDto {
    @NotEmpty
    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private long category;

    @NotEmpty
    @Size(min = 20, max = 7000)
    private String description;

    @NotEmpty
    private String eventDate;

    @NotNull
    private Location location;

    private boolean paid = false;

    private int participantLimit = 0;

    private boolean requestModeration = true;

    @NotEmpty
    @Size(min = 3, max = 120)
    private String title;
}