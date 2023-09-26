package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    Page<Compilation> findByPinned(boolean pinned, Pageable page);
}