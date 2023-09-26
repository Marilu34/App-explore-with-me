package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Event;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompilationDto {
    private Integer id;
    private List<Event> events;
    private boolean pinned;
    private String title;
}