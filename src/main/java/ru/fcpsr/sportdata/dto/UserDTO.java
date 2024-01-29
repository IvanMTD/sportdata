package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fcpsr.sportdata.models.Role;

@Data
@RequiredArgsConstructor
public class UserDTO {
    private int id;
    private String username;
    private String password;
    private String confirm;
    private String email;
    private Role role;
}
