package ewm.stats.server.model.dto;

import ewm.stats.server.model.StatRecord;
import org.junit.jupiter.api.Test;
import ewm.stats.dto.StatRecordCreateDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatDtoMapperTest {

    @Test
    void toStatRecord() {
        StatRecordCreateDto statRecordCreateDto = StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        StatRecord statRecord = StatRecord.builder()
                .appName("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        StatRecord mappedStatRecord = StatDtoMapper.toStat(statRecordCreateDto);
        assertEquals(statRecord, mappedStatRecord);
    }
}