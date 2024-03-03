package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class AgeGroupDTO {
    private int id;
    private int disciplineId;
    @NotBlank(message = "Вы пытались добавить/обновить возрастную группу с пустым полем название. Поле не может быть пустым!")
    private String title;
    private int minAge;
    private int maxAge;
    private DisciplineDTO discipline;
}
