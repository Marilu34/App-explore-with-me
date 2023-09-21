package ewm.stats.server.service;

import ewm.stats.dto.EndpointDto;
import ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    void saveEndpoint(EndpointDto endpointDto);

    List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}