package ewm.main.service.compilation;

import ewm.main.service.compilation.model.Compilation;
import ewm.main.service.compilation.model.dto.ShortCompilationDto;
import ewm.main.service.compilation.model.dto.CompilationRequest;
import ewm.main.service.error_handler.NotFoundException;
import ewm.main.service.event.EventRepository;
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

    private final EventRepository eventRepository;

    private final CompilationRepository compilationRepository;

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

    public Compilation update(CompilationRequest updateRequest, long compilationId) {
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

    public Compilation getCompilationById(long compilationId) {
        Optional<Compilation> optionalCompilation = compilationRepository.findById(compilationId);
        if (optionalCompilation.isEmpty()) {
            throw new NotFoundException("Компиляция " + compilationId + " не обнаружена");
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

    public void delete(long compilationId) {
        compilationRepository.delete(getCompilationById(compilationId));
    }
}