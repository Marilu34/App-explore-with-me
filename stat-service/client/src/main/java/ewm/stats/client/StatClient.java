package ewm.stats.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewm.stats.dto.StatRecordCreateDto;
import ewm.stats.dto.ViewStatsDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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

@Component
@Slf4j
public class StatClient {
    private final String appName;
    private final String serverUrl;
    private final HttpClient httpClient;
    private final ObjectMapper json;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatClient(@Value("${spring.application.name}") String appName,
                      @Value("${stat-server.url}") String serverUrl,
                      ObjectMapper json) {
        this.appName = appName;
        this.serverUrl = serverUrl;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        this.json = json;
    }

    public void newStatRecord(String uri, String ip, LocalDateTime timestamp) {
        StatRecordCreateDto statRecordCreateDto = StatRecordCreateDto.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(timestamp.format(DATE_TIME_FORMATTER))
                .build();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(statRecordCreateDto)))
                    .uri(URI.create(serverUrl + "/hit"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == HttpStatus.CREATED.value()) {
                log.info("New stat record success: {}", statRecordCreateDto);
            } else {
                log.warn("New stat record failed: {}", statRecordCreateDto);
            }
        } catch (Exception e) {
            log.warn("New stat record exception: " + e.getMessage() + ", data: {}", statRecordCreateDto);
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
                    .uri(URI.create(serverUrl + "/stats?" + queryString))
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                List<ViewStatsDto> statsList = json.readValue(response.body(), new TypeReference<>() {
                });

                log.info("Stats received for: {} - {}, {}, {}. Received {} records",
                        start,
                        end,
                        uris != null ? String.join(",", uris) : "null",
                        unique != null ? unique.toString() : "null",
                        statsList.size());

                return statsList;
            } else {
                log.warn("Stats receive failed for: {} - {}, {}, {}",
                        start,
                        end,
                        uris != null ? String.join(",", uris) : "null",
                        unique != null ? unique.toString() : "null");
            }
        } catch (Exception e) {
            log.warn("Stats receive failed because exception for: {} - {}, {}, {}. Exception message: " + e.getMessage(),
                    start,
                    end,
                    uris != null ? String.join(",", uris) : "null",
                    unique != null ? unique.toString() : "null");
        }

        return List.of();
    }
}