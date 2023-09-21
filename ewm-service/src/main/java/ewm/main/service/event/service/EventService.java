package ewm.main.service.event.service;

import ewm.main.service.event.dto.EventDto;
import ewm.main.service.event.dto.EventShortDto;
import ewm.main.service.event.dto.NewEventDto;
import ewm.main.service.event.dto.UpdateEventDto;
import ewm.main.service.event.model.Admin;
import ewm.main.service.event.model.Public;

import java.util.List;

public interface EventService {
    EventDto createEvent(Long userId, NewEventDto newEventDto);

    EventDto updateEvent(Long userId, Long eventId, UpdateEventDto updateEventDto);

    EventDto updateEventAdmin(Long eventId, UpdateEventDto updateEventDto);

    List<EventDto> getAdminEvent(Admin admin);

    List<EventDto> getEvents(Public publicEvents);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventDto getEventById(Long id, String ip);

    EventDto getUserEventById(Long userId, Long eventId);
}