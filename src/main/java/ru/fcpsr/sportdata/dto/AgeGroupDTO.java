package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.AgeGroup;

import java.time.LocalDate;


@Data
@NoArgsConstructor
public class AgeGroupDTO {
    private int id;
    private int sportId;
    @NotBlank(message = "Вы пытались добавить/обновить возрастную группу с пустым полем название. Поле не может быть пустым!")
    private String title;
    @Min(value = 3, message = "Нельзя установить минимальный возраст меньше 3 лет")
    @Max(value = 25, message = "Нельзя установить минимальный возраст больше 25 лет")
    private int minAge;
    @Min(value = 3, message = "Нельзя установить максимальный возраст меньше 3 лет")
    @Max(value = 25, message = "Нельзя установить максимальный возраст больше 25 лет")
    private int maxAge;
    private TypeOfSportDTO sport;

    public AgeGroupDTO(AgeGroup group) {
        setId(group.getId());
        setTitle(group.getTitle());
        setMinAge(group.getMinAge());
        setMaxAge(group.getMaxAge());
    }

    public int getMinAgeYear(){
        return LocalDate.now().getYear() - maxAge;
    }

    public int getMaxAgeYear(){
        return LocalDate.now().getYear() - minAge;
    }

    public String getLowTitle(){
        if(title != null) {
            return title.toLowerCase();
        }else{
            return "";
        }
    }
}
