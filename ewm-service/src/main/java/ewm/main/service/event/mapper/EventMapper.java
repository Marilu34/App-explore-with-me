package ewm.main.service.event.mapper;

import ewm.main.service.category.model.Category;
import ewm.main.service.category.mapper.CategoryMapper;
import ewm.main.service.event.dto.EventDto;
import ewm.main.service.event.dto.EventShortDto;
import ewm.main.service.event.dto.NewEventDto;
import ewm.main.service.event.dto.UpdateEventDto;
import ewm.main.service.event.model.Event;
import ewm.main.service.event.model.EventState;
import ewm.main.service.event.model.Location;
import ewm.main.service.user.mapper.UserMapper;
import ewm.main.service.user.model.User;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequest())
                .build();
    }

    public Event updateEvent(UpdateEventDto updateEventDto, Event event, Category category, EventState state) {
        return Event.builder()
                .annotation(updateEventDto.getAnnotation() != null ?
                        updateEventDto.getAnnotation() : event.getAnnotation())
                .category(category)
                .paid(updateEventDto.getPaid() != null ?
                        updateEventDto.getPaid() : event.getPaid())
                .participantLimit(updateEventDto.getParticipantLimit() != null ?
                        updateEventDto.getParticipantLimit() : event.getParticipantLimit())
                .requestModeration(updateEventDto.getRequestModeration() != null ?
                        updateEventDto.getRequestModeration() : event.getRequestModeration())
                .state(state)
                .title(updateEventDto.getTitle() != null ?
                        updateEventDto.getTitle() : event.getTitle())
                .confirmedRequest(event.getConfirmedRequest())
                .createdOn(event.getCreatedOn())
                .id(event.getId())
                .initiator(event.getInitiator())
                .publishedOn(event.getPublishedOn())
                .views(event.getViews())
                .description(updateEventDto.getDescription() != null ?
                        updateEventDto.getDescription() : event.getDescription())
                .eventDate(updateEventDto.getEventDate() != null ?
                        updateEventDto.getEventDate() : event.getEventDate())
                .lon(updateEventDto.getLocation() != null ?
                        updateEventDto.getLocation().getLon() : event.getLon())
                .lat(updateEventDto.getLocation() != null ?
                        updateEventDto.getLocation().getLat() : event.getLat())
                .build();
    }

    public Event toEvent(NewEventDto newEventDto, User user, Category category) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .build();
    }

    public EventDto toEventFullDto(Event event) {
        return EventDto.builder()
                .annotation(event.getAnnotation())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(Location.builder()
                        .lon(event.getLon())
                        .lat(event.getLat())
                        .build())
                .paid(event.getPaid())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .views(event.getViews())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequest())
                .createdOn(event.getCreatedOn())
                .build();
    }
}