package ewm.stats.server.service;

import ewm.stats.server.model.StatRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ewm.stats.dto.StatRecordCreateDto;
import ewm.stats.server.repository.StatRecordRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatRecordServiceTest {

    private StatRecordRepository mockStatRecordRepository;

    private StatRecordService statRecordService;

    @BeforeEach
    void beforeEach() {
        mockStatRecordRepository = Mockito.mock(StatRecordRepository.class);
        statRecordService = new StatRecordService(mockStatRecordRepository);
    }

    @Test
    void newStatRecordTest() {
        StatRecordCreateDto statRecordCreateDto = StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        StatRecord statRecord = StatRecord.builder()
                .id(1)
                .appName("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        Mockito
                .when(mockStatRecordRepository.save(Mockito.any(StatRecord.class)))
                .thenReturn(statRecord);

        StatRecord savedStatRecord = statRecordService.newStatRecord(statRecordCreateDto);
        assertEquals(statRecord, savedStatRecord);
    }

    @Test
    void getStats() {
        StatRecordCreateDto statRecordCreateDto = StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        StatRecord statRecord = StatRecord.builder()
                .id(1)
                .appName("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        Mockito
                .when(mockStatRecordRepository.save(Mockito.any(StatRecord.class)))
                .thenReturn(statRecord);

        StatRecord savedStatRecord = statRecordService.newStatRecord(statRecordCreateDto);
        assertEquals(statRecord, savedStatRecord);
    }
}