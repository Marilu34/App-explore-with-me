package ewm.stats.server.mapper;

import ewm.stats.dto.EndpointDto;
import ewm.stats.server.model.Endpoint;
import lombok.experimental.UtilityClass;
@UtilityClass
public class EndpointMapper {

    public Endpoint toEndpoint(EndpointDto endpointDto) {
        return Endpoint.builder()
                .appName(endpointDto.getApp())
                .appUri(endpointDto.getUri())
                .ip(endpointDto.getIp())
                .timestamp(endpointDto.getTimestamp())
                .build();
    }
}