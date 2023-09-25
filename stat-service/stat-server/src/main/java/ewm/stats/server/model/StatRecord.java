package ewm.stats.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stat_records")
@Builder
public class StatRecord {
    /**
     * Идентификатор записи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    private long id;

    /**
     * Идентификатор сервиса для которого записывается информация
     */
    @Column(name = "app_name", nullable = false)
    private String appName;

    /**
     * URI для которого был осуществлен запрос
     */
    @Column(name = "uri", nullable = false)
    private String uri;

    /**
     * IP-адрес пользователя, осуществившего запрос
     */
    @Column(name = "ip", nullable = false)
    private String ip;

    /**
     * Дата и время, когда был совершен запрос к эндпоинту (в формате "yyyy-MM-dd HH:mm:ss")
     */
    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timestamp;
}