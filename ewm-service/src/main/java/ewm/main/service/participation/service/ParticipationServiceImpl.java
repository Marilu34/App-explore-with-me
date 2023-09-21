package ewm.main.service.participation.service;

import ewm.main.service.event.model.Event;
import ewm.main.service.event.model.EventState;
import ewm.main.service.event.repository.EventRepository;
import ewm.main.service.exception.NotFoundException;
import ewm.main.service.exception.ParticipationRequestFailException;
import ewm.main.service.participation.dto.ParticipationRequestDto;
import ewm.main.service.participation.mapper.ParticipationMapper;
import ewm.main.service.participation.model.EventRequestQuery;
import ewm.main.service.participation.model.EventRequestResponse;
import ewm.main.service.participation.model.ParticipationRequest;
import ewm.main.service.participation.model.ParticipationStatus;
import ewm.main.service.participation.repository.ParticipationRepository;
import ewm.main.service.user.model.User;
import ewm.main.service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {

    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ParticipationRequestFailException("Event exists"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not exists"));
        if (participationRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ParticipationRequestFailException("Request exists");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ParticipationRequestFailException("Not enough rights");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ParticipationRequestFailException("Not published event");
        }
        if (event.getParticipantLimit() <= event.getConfirmedRequest() && event.getParticipantLimit() != 0) {
            throw new ParticipationRequestFailException("No vacancies");
        }
        ParticipationRequest request = ParticipationRequest.builder().created(LocalDateTime.now()).event(event)
                .requester(user).status(event.getParticipantLimit() == 0 || !event.getRequestModeration() ?
                        ParticipationStatus.CONFIRMED : ParticipationStatus.PENDING).build();
        if (request.getStatus().equals(ParticipationStatus.CONFIRMED)) {
            event.setConfirmedRequest(event.getConfirmedRequest() + 1);
            eventRepository.save(event);
        }
        return ParticipationMapper.toParticipationEventDto(participationRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequest(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not exists"));
        return participationRepository.findAllByRequesterId(userId).stream().map(ParticipationMapper::toParticipationEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not exists"));
        participationRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Request not exists"));
        ParticipationRequest participationRequest = participationRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("Request not exists"));
        participationRequest.setStatus(ParticipationStatus.CANCELED);
        return ParticipationMapper.toParticipationEventDto(participationRepository.save(participationRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not exists"));
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not exists"));
        return participationRepository.findAllByEventId(eventId).stream().map(ParticipationMapper::toParticipationEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestResponse changeRequestStatus(Long userId, Long eventId, EventRequestQuery eventRequestQuery) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not exists"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not exists"));
        Long alreadyConfirmed = participationRepository.findConfirmed(eventId);

        if (event.getParticipantLimit() <= alreadyConfirmed) {
            throw new ParticipationRequestFailException("No vacancies");
        }

        List<ParticipationRequest> queryRequests = participationRepository.findAllByIdIn(eventRequestQuery.getRequestIds());
        if (!queryRequests.isEmpty()) {
            for (ParticipationRequest request : queryRequests) {
                if (!ParticipationStatus.PENDING.equals(request.getStatus())) {
                    throw new ParticipationRequestFailException("Not all state PENDING");
                }
            }
        }

        if (ParticipationStatus.REJECTED.equals(eventRequestQuery.getStatus())) {
            queryRequests.forEach(request -> request.setStatus(ParticipationStatus.REJECTED));
        } else {
            long availableVacancies = event.getParticipantLimit() - alreadyConfirmed;
            long numConfirmed = Math.min(availableVacancies, queryRequests.size());

            for (int i = 0; i < numConfirmed; i++) {
                ParticipationRequest request = queryRequests.get(i);
                request.setStatus(ParticipationStatus.CONFIRMED);
            }

            for (long i = numConfirmed; i < queryRequests.size(); i++) {
                ParticipationRequest request = queryRequests.get((int) i);
                request.setStatus(ParticipationStatus.REJECTED);
            }

            event.setConfirmedRequest(alreadyConfirmed + numConfirmed);
            eventRepository.save(event);
        }

        List<ParticipationRequest> savedRequests = participationRepository.saveAll(queryRequests);
        return ParticipationMapper.toEventRequestStatusUpdateResult(savedRequests);
    }
}