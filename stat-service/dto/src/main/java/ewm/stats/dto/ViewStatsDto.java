package ewm.stats.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
public final class ViewStatsDto {
    private final String app;
    private final String uri;
    private final Long hits;
}