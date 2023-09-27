package ru.practicum.db;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationDtoRequest;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.model.Event;
import ru.practicum.repository.EventRepository;
import ru.practicum.exception.ResourceNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.services.CompilationService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CompilationServiceDB implements CompilationService {

    private final CompilationRepository repository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(CompilationDtoRequest compilationDto) {
        List<Event> events = new ArrayList<>();
        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }
        if (compilationDto.getTitle() == null || compilationDto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Заголовок не может быть пустым!");
        }
        if (compilationDto.getTitle().length() > 50) {
            throw new ValidationException("Слишком длинный заголовок!");
        }
        if (compilationDto.getEvents() != null) {
            for (Integer id : compilationDto.getEvents()) {
                events.add(eventRepository.findById(id)
                        .orElseThrow(() -> new ValidationException("Такого события нет!")));
            }
        }
        return CompilationMapper.toCompilationDto(repository.save(new Compilation(compilationDto.getId(),
                events,
                compilationDto.getPinned(),
                compilationDto.getTitle())));
    }

    @Override
    @Transactional
    public void deleteCompilation(Integer compId) {
        repository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Integer compId, CompilationDtoRequest compilationDto) {
        Compilation oldCompilation = repository.findById(compId)
                .orElseThrow(() -> new ValidationException("Такой подборки нет!"));
        if (compilationDto.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Integer id : compilationDto.getEvents()) {
                events.add(eventRepository.findById(id)
                        .orElseThrow(() -> new ValidationException("Такого события нет!")));
            }
            oldCompilation.setEvents(events);
        }
       if (compilationDto.getPinned() != null) {
           oldCompilation.setPinned(compilationDto.getPinned());
       }
       if (compilationDto.getTitle() != null) {
           if (compilationDto.getTitle().length() > 50) {
               throw new ValidationException("Слишком длинный заголовок!");
           }
           oldCompilation.setTitle(compilationDto.getTitle());
       }
        return CompilationMapper.toCompilationDto(repository.save(oldCompilation));
    }

    @Override
    public List<CompilationDto> getAllCompilation(Boolean pinned, Integer from, Integer size) {
        return CompilationMapper.toCompilationDtoList(repository.findByPinned(pinned,
                PageRequest.of(from, size)).toList());
    }

    @Override
    public CompilationDto getCompilation(Integer id) {
        return CompilationMapper.toCompilationDto(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Такой подборки нет!")));
    }
}