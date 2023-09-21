package ewm.main.service.event.standart;

import ewm.main.service.event.model.EventState;
import ewm.main.service.event.model.SortParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventStandart {
    private List<Long> users;
    private String text;
    private Boolean onlyAvailable;
    private SortParameter sortParam;
    private List<Integer> categories;
    private Boolean paid;
    private List<EventState> states;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
}