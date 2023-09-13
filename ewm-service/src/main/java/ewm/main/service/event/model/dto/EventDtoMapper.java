package ewm.main.service.event.model.dto;

import ewm.main.service.category.model.dto.CategoryDtoMapper;
import ewm.main.service.common.models.EventState;
import ewm.main.service.event.model.Event;
import ewm.main.service.user.model.dto.UserDtoMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ewm.main.service.common.EwmConstants.DATE_TIME_FORMATTER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventDtoMapper {
    public static EventFullDto toEventFullDto(Event event) {
        if (event != null) {
            return EventFullDto.builder()
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

    public static List<EventFullDto> toEventFullDtoList(Collection<Event> allEvents) {
        if (allEvents != null) {
            List<EventFullDto> eventFullDtoList = new ArrayList<>();
            for (Event event : allEvents) {
                eventFullDtoList.add(toEventFullDto(event));
            }
            return eventFullDtoList;
        } else {
            return null;
        }
    }

    public static Event toEvent(EventFullDto eventFullDto) {
        if (eventFullDto != null) {
            return Event.builder()
                    .id(eventFullDto.getId())
                    .annotation(eventFullDto.getAnnotation())
                    .category(CategoryDtoMapper.toCategory(eventFullDto.getCategory()))
                    .confirmedRequests(eventFullDto.getConfirmedRequests())
                    .createdOn(LocalDateTime.parse(eventFullDto.getCreatedOn(), DATE_TIME_FORMATTER))
                    .description(eventFullDto.getDescription())
                    .eventDate(LocalDateTime.parse(eventFullDto.getEventDate(), DATE_TIME_FORMATTER))
                    .initiator(UserDtoMapper.toUser(eventFullDto.getInitiator()))
                    .location(eventFullDto.getLocation())
                    .paid(eventFullDto.isPaid())
                    .participantLimit(eventFullDto.getParticipantLimit())
                    .publishedOn(LocalDateTime.parse(eventFullDto.getPublishedOn(), DATE_TIME_FORMATTER))
                    .requestModeration(eventFullDto.isRequestModeration())
                    .state(EventState.valueOf(eventFullDto.getState()))
                    .title(eventFullDto.getTitle())
                    .views(eventFullDto.getViews())
                    .build();
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
