package ewm.main.service.compilation.model;

import ewm.main.service.event.model.Event;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilation")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean pinned;
    @Column(name = "title", length = 150)
    private String title;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_events", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;
}