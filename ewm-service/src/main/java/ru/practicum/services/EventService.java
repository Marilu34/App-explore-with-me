package ru.practicum.services;

import ru.practicum.dto.EventDto;
import ru.practicum.dto.EventDtoRequest;
import ru.practicum.model.State;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    public List<EventDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime start,
                                    LocalDateTime end, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                    HttpServletRequest request);

    public EventDto getEvent(Integer id, HttpServletRequest request);

    public List<EventDto> getAdminEvents(List<Long> users, List<State> states, List<Long> categories,
                                         String start, String end, Integer from, Integer size);

    public EventDto patchAdminEvent(Integer eventId, EventDtoRequest eventDtoRequest);
}