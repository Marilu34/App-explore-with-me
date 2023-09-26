package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Event;
import ru.practicum.model.Status;
import ru.practicum.model.UserRequest;
import ru.practicum.model.User;

import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequest, Integer> {

    List<UserRequest> findAllByRequester(User requester);

    List<UserRequest> findAllByEvent(Event event);

    List<UserRequest> findAllByStatusAndRequester(Status status, User requester);
}