package ewm.stats.server.service.impl;

import ewm.stats.dto.EndpointDto;
import ewm.stats.dto.ViewStatsDto;
import ewm.stats.server.exception.WrongTimeException;
import ewm.stats.server.mapper.EndpointMapper;
import ewm.stats.server.repository.StatisticsServerServiceRepository;
import ewm.stats.server.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsServerServiceRepository statisticsServerServiceRepository;

    @Override
    @Transactional
    public void saveEndpoint(EndpointDto endpointDto) {
        statisticsServerServiceRepository.save(EndpointMapper.toEndpoint(endpointDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new WrongTimeException("Start after end time");
        }
        if (unique != null && unique) {
            if (uris.isEmpty()) {
                return statisticsServerServiceRepository.findStatsWithUris(start, end, null);
            } else {
                return statisticsServerServiceRepository.findStatsWithUris(start, end, uris);
            }
        } else {
            if (uris == null) {
                return statisticsServerServiceRepository.findStatsWithoutUris(start, end, null);
            } else {
                return statisticsServerServiceRepository.findStatsWithoutUris(start, end, uris);
            }
        }
    }
}