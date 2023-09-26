package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "requests")
public class UserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "created")
    private LocalDateTime created;

    @OneToOne
    @JoinColumn(name = "event")
    private Event event;

    @OneToOne
    @JoinColumn(name = "requester")
    private User requester;

    @Column(name = "status")
    private Status status;
}