package ewm.main.service.event.model.dto;

import ewm.main.service.category.model.dto.CategoryDtoMapper;
import ewm.main.service.event.model.Event;
import ewm.main.service.user.model.dto.UserDtoMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ewm.main.service.common.Date.DATE_TIME_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventDtoMapper {
    public static EventDto toEventDto(Event event) {
        if (event != null) {
            return EventDto.builder()
                    .id(event.getId())
                    .annotation(event.getAnnotation())
                    .category(CategoryDtoMapper.toCategoryDto(event.getCategory()))
                    .confirmedRequests(event.getConfirmedRequests())
                    .createdOn(event.getCreatedOn().format(DATE_TIME_FORMATTER))
                    .description(event.getDescription())
                    .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                    .initiator(UserDtoMapper.toUserShortDto(event.getInitiator()))
                    .location(event.getLocation())
                    .paid(event.getPaid())
                    .participantLimit(event.getParticipantLimit())
                    .publishedOn(event.getPublishedOn().format(DATE_TIME_FORMATTER))
                    .requestModeration(event.getRequestModeration())
                    .state(event.getState().toString())
                    .title(event.getTitle())
                    .views(event.getViews())
                    .build();
        } else {
            return null;
        }
    }

    public static List<EventDto> toEventDtoList(Collection<Event> allEvents) {
        if (allEvents != null) {
            List<EventDto> eventDtoList = new ArrayList<>();
            for (Event event : allEvents) {
                eventDtoList.add(toEventDto(event));
            }
            return eventDtoList;
        } else {
            return null;
        }
    }

    public static EventShortDto toEventShortDto(Event event) {
        if (event != null) {
            return EventShortDto.builder()
                    .id(event.getId())
                    .annotation(event.getAnnotation())
                    .category(CategoryDtoMapper.toCategoryDto(event.getCategory()))
                    .confirmedRequests(event.getConfirmedRequests())
                    .eventDate(event.getEventDate().format(DATE_TIME_FORMATTER))
                    .initiator(UserDtoMapper.toUserShortDto(event.getInitiator()))
                    .paid(event.getPaid())
                    .title(event.getTitle())
                    .views(event.getViews())
                    .build();
        } else {
            return null;
        }
    }

    public static List<EventShortDto> toEventShortDtoList(Collection<Event> eventList) {
        if (eventList != null) {
            List<EventShortDto> eventShortDtoList = new ArrayList<>();
            for (Event event : eventList) {
                eventShortDtoList.add(toEventShortDto(event));
            }
            return eventShortDtoList;
        } else {
            return null;
        }
    }
}
