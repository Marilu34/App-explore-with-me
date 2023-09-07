package ewm.stats.server.service;

import ewm.stats.server.model.StatRecord;
import ewm.stats.server.model.dto.StatDtoMapper;
import ewm.stats.server.repository.StatRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ewm.stats.dto.StatRecordCreateDto;
import ewm.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatRecordService {
    private final StatRecordRepository statRecordRepository;

    public StatRecord newStatRecord(StatRecordCreateDto statRecordCreateDto) {
        StatRecord statRecord = StatDtoMapper.toStat(statRecordCreateDto);
        return statRecordRepository.save(statRecord);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris.isEmpty()) {
            if (!unique) {
                return statRecordRepository.findStats(start, end);
            } else {
                return statRecordRepository.findStatsUniqueIp(start, end);
            }
        } else {
            if (!unique) {
                return statRecordRepository.findStatsForUriList(start, end, uris);
            } else {
                return statRecordRepository.findStatsForUriListAndUniqueIp(start, end, uris);
            }
        }
    }
}
