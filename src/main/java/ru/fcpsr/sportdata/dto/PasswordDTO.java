package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordDTO {
    @NotBlank(message = "Поле не может быть пустым")
    private String oldPassword;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Не менее 8 знаков")
    private String newPassword;
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Не менее 8 знаков")
    private String newPasswordConfirm;
}
