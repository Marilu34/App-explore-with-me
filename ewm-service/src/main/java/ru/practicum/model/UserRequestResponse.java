package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.UserRequestDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestResponse {
    private List<UserRequestDto> confirmedRequests;
    private List<UserRequestDto> rejectedRequests;
}