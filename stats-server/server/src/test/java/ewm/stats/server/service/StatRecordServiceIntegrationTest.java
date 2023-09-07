package ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ewm.stats.dto.StatRecordCreateDto;
import ewm.stats.dto.ViewStatsDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(
        properties = "spring.profiles.active=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatRecordServiceIntegrationTest {

    private final StatRecordService statRecordService;
    private final LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void beforeEach() {
        statRecordService.newStatRecord(StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(time.format(dateTimeFormat))
                .build());

        statRecordService.newStatRecord(StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(time.plusMinutes(5).format(dateTimeFormat))
                .build());

        statRecordService.newStatRecord(StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.2")
                .timestamp(time.format(dateTimeFormat))
                .build());

        statRecordService.newStatRecord(StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/other-uri")
                .ip("1.1.1.2")
                .timestamp(time.format(dateTimeFormat))
                .build());

        statRecordService.newStatRecord(StatRecordCreateDto.builder()
                .app("other-app")
                .uri("/test/other-app-uri")
                .ip("1.1.1.1")
                .timestamp(time.format(dateTimeFormat))
                .build());

        statRecordService.newStatRecord(StatRecordCreateDto.builder()
                .app("other-app")
                .uri("/test/other-app-uri")
                .ip("1.1.1.1")
                .timestamp(time.plusMinutes(5).format(dateTimeFormat))
                .build());
    }

    @Test
    void getStatsAllTest() {
        List<ViewStatsDto> viewStatsList = statRecordService.getStats(time, time.plusMinutes(10), List.of(), false);
        ViewStatsDto viewStatsDto1 = new ViewStatsDto("test-app", "/test/uri", 3);
        ViewStatsDto viewStatsDto2 = new ViewStatsDto("test-app", "/test/other-uri", 1);
        ViewStatsDto viewStatsDto3 = new ViewStatsDto("other-app", "/test/other-app-uri", 2);

        assertTrue(viewStatsList.contains(viewStatsDto1));
        assertTrue(viewStatsList.contains(viewStatsDto2));
        assertTrue(viewStatsList.contains(viewStatsDto3));
    }

    @Test
    void getStatsWithUriListTest() {
        List<ViewStatsDto> viewStatsList = statRecordService.getStats(time, time.plusMinutes(10), List.of("/test/other-uri"), false);
        ViewStatsDto viewStatsDto1 = new ViewStatsDto("test-app", "/test/other-uri", 1);

        assertTrue(viewStatsList.contains(viewStatsDto1));
    }

    @Test
    void getStatsAllWithUniqueTest() {
        List<ViewStatsDto> viewStatsList = statRecordService.getStats(time, time.plusMinutes(10), List.of(), true);
        ViewStatsDto viewStatsDto1 = new ViewStatsDto("test-app", "/test/uri", 2);
        ViewStatsDto viewStatsDto2 = new ViewStatsDto("test-app", "/test/other-uri", 1);
        ViewStatsDto viewStatsDto3 = new ViewStatsDto("other-app", "/test/other-app-uri", 1);

        assertTrue(viewStatsList.contains(viewStatsDto1));
        assertTrue(viewStatsList.contains(viewStatsDto2));
        assertTrue(viewStatsList.contains(viewStatsDto3));
    }

    @Test
    void getStatsAllWithUriListAndUniqueTest() {
        List<ViewStatsDto> viewStatsList = statRecordService.getStats(time, time.plusMinutes(10), List.of("/test/uri", "/test/other-uri"), true);
        ViewStatsDto viewStatsDto1 = new ViewStatsDto("test-app", "/test/uri", 2);
        ViewStatsDto viewStatsDto2 = new ViewStatsDto("test-app", "/test/other-uri", 1);

        assertTrue(viewStatsList.contains(viewStatsDto1));
        assertTrue(viewStatsList.contains(viewStatsDto2));
    }
}