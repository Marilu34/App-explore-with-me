package ewm.main.service.compilation.model;

import ewm.main.service.event.model.Event;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id", nullable = false)
    private long id;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST,
                    CascadeType.MERGE})
    @JoinTable(name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    @Column(name = "pinned", nullable = false)
    private boolean pinned;

    @Column(name = "title", length = 150, nullable = false)
    private String title;
}
