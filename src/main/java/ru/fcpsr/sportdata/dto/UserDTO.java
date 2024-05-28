package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.fcpsr.sportdata.enums.Role;
import ru.fcpsr.sportdata.models.SysUser;

@Data
@RequiredArgsConstructor
public class UserDTO {
    private int id;
    @NotBlank(message = "Не может быть пустым")
    @Size(min = 4, message = "Минимум 4 знака")
    private String username;
    @NotBlank(message = "Не может быть пустым")
    @Size(min = 8, message = "Минимум 8 знака")
    private String password;
    @NotBlank(message = "Не может быть пустым")
    @Size(min = 8, message = "Минимум 8 знака")
    private String confirm;
    @NotBlank(message = "Не может быть пустым")
    @Email(message = "Укажите валидный e-mail")
    private String email;
    private Role role;

    public UserDTO(SysUser user){
        setId(user.getId());
        setEmail(user.getEmail());
        setUsername(user.getUsername());
        setRole(user.getRole());
    }
}
