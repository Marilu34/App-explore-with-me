package ewm.stats.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ewm.stats.dto.StatRecordCreateDto;
import ewm.stats.dto.ViewStatsDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatClient {
    private final String appName;
    private final String url;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatClient(@Value("$spring.application.name") String appName,
                      @Value("${stat-server.url}") String url,
                      ObjectMapper objectMapper) {
        this.appName = appName;
        this.url = url;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        this.objectMapper = objectMapper;
    }

    public void newStat(String uri, String ip, LocalDateTime timestamp) {
        StatRecordCreateDto statRecordCreateDto = StatRecordCreateDto.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp.format(DATE_TIME_FORMATTER))
                .build();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(statRecordCreateDto)))
                    .uri(URI.create(url + "/hit"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == HttpStatus.CREATED.value()) {
                log.info("Новая запись успешно создана: {}", statRecordCreateDto);
            } else {
                log.warn("Новая запись не может быть создана: {}", statRecordCreateDto);
            }
        } catch (Exception e) {
            log.warn("При создании новой записи возникла ошибка: " + e.getMessage() + ", дата: {}", statRecordCreateDto);
        }
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start.format(DATE_TIME_FORMATTER),
                "end", end.format(DATE_TIME_FORMATTER),
                "uris", uris != null ? String.join(",", uris) : "",
                "unique", unique != null ? unique.toString() : "");

        try {
            String queryString = parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url + "/stats?" + queryString))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                List<ViewStatsDto> statsList = objectMapper.readValue(response.body(), new TypeReference<>() {
                });

                log.info("Статистика, полученная за: {} - {}, {}, {}.Получены {} записи",
                        start,
                        end,
                        uris != null ? String.join(",", uris) : "null",
                        unique != null ? unique.toString() : "null",
                        statsList.size());

                return statsList;
            } else {
                log.warn("Не удалось получить статистику для: {} - {}, {}, {}",
                        start,
                        end,
                        uris != null ? String.join(",", uris) : "null",
                        unique != null ? unique.toString() : "null");
            }
        } catch (Exception e) {
            log.warn("Не удалось получить статистику для : {} - {}, {}, {}. Произошла ошибка: " + e.getMessage(),
                    start,
                    end,
                    uris != null ? String.join(",", uris) : "null",
                    unique != null ? unique.toString() : "null");
        }

        return List.of();
    }
}