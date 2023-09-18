package ewm.main.service.compilation;

import ewm.main.service.compilation.model.Compilation;
import ewm.main.service.compilation.model.dto.ShortCompilationDto;
import ewm.main.service.compilation.model.dto.UpdateCompilationRequest;
import ewm.main.service.event.EventRepository;
import ewm.main.service.exceptions.CompilationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    public Compilation getCompilationById(long compilationId) {
        Optional<Compilation> optionalCompilation = compilationRepository.findById(compilationId);
        if (optionalCompilation.isEmpty()) {
            throw new CompilationNotFoundException("Компиляция " + compilationId + " не обнаружена");
        } else {
            return optionalCompilation.get();
        }
    }

    public List<Compilation> getAllCompilations(Boolean pinned, int from, int size) {
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, PageRequest.of(from / size, size));
        } else {
            return compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
    }

    public Compilation create(ShortCompilationDto shortCompilationDto) {
        Compilation compilation = Compilation.builder()
                .title(shortCompilationDto.getTitle())
                .pinned(shortCompilationDto.getPinned())
                .build();
        if (shortCompilationDto.getEvents() != null && !shortCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(Set.copyOf(eventRepository.findByIdIn(List.copyOf(shortCompilationDto.getEvents()))));
        }

        return compilationRepository.save(compilation);
    }

    public Compilation update(UpdateCompilationRequest updateRequest, long compilationId) {
        Compilation compilation = getCompilationById(compilationId);
        if (updateRequest.getEvents() != null) {
            if (updateRequest.getEvents().isEmpty()) {
                compilation.setEvents(new HashSet<>());
            } else {
                compilation.setEvents(eventRepository.findByIdIn(List.copyOf(updateRequest.getEvents())));
            }
        }

        if (updateRequest.getPinned() != null) compilation.setPinned(updateRequest.getPinned());
        if (updateRequest.getTitle() != null) compilation.setTitle(updateRequest.getTitle());

        return compilationRepository.save(compilation);
    }

    public void delete(long compilationId) {
        compilationRepository.delete(getCompilationById(compilationId));
    }
}