package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Commentary;

public interface CommentaryRepository extends JpaRepository<Commentary, Integer> {
}