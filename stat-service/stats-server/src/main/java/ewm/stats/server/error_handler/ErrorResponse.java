package ewm.stats.server.error_handler;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
    private final String description;
}
