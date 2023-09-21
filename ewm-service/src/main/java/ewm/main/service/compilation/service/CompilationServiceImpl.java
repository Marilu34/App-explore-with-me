package ewm.main.service.compilation.service;

import ewm.main.service.compilation.dto.CompilationDto;
import ewm.main.service.compilation.dto.NewCompilationDto;
import ewm.main.service.compilation.mapper.CompilationMapper;
import ewm.main.service.compilation.model.Compilation;
import ewm.main.service.compilation.repository.CompilationRepository;
import ewm.main.service.compilation.repository.CompilationBase;
import ewm.main.service.event.model.Event;
import ewm.main.service.event.repository.EventRepository;
import ewm.main.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compilationId, NewCompilationDto newCompilationDto) {
        Compilation updateCompilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation not exist"));
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        updateCompilation = CompilationMapper.updateCompilation(newCompilationDto, updateCompilation, events);
        return CompilationMapper.toCompilationDto(compilationRepository.save(updateCompilation));
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compilationId) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id %d does not exist")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAllCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAll(new CompilationBase(pinned), pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        compilationRepository.findById(compilationId).orElseThrow(() -> new NotFoundException("Compilation not exists"));
        compilationRepository.deleteById(compilationId);
    }
}