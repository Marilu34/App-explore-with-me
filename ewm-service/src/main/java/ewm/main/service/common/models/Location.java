package ewm.main.service.common.models;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    private float lat; //широта
    private float lon;
}
