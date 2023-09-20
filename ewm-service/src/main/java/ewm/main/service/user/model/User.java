package ewm.main.service.user.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private long id;

    private String name;

    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User event = (User) o;
        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}