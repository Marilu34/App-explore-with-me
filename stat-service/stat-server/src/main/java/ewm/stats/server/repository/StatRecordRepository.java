package ewm.stats.server.repository;

import ewm.stats.dto.ViewStatsDto;
import ewm.stats.server.model.StatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRecordRepository extends JpaRepository<StatRecord, Long> {

    @Query("SELECT new ewm.stats.dto.ViewStatsDto(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM StatRecord s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> findStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ewm.stats.dto.ViewStatsDto(s.appName, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM StatRecord s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> findStatsUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ewm.stats.dto.ViewStatsDto(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM StatRecord s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 AND s.uri IN ?3 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStatsDto> findStatsForUriList(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ewm.stats.dto.ViewStatsDto(s.appName, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM StatRecord s " +
            "WHERE s.timestamp >= ?1 AND s.timestamp < ?2 AND s.uri IN ?3 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStatsDto> findStatsForUriListAndUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
