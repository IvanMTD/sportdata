package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Discipline;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DisciplineDTO {
    private int id;
    private int sportId;
    @NotBlank(message = "Вы пытались добавить/обновить дисциплину с пустым полем названия. Поле не может быть пустым!")
    private String title;
    private TypeOfSportDTO typeOfSport;
    private List<AgeGroupDTO> ageGroups = new ArrayList<>();

    public void addAgeGroup(AgeGroupDTO ageGroupDTO) {
        ageGroups.add(ageGroupDTO);
    }

    public DisciplineDTO(Discipline discipline){
        setId(discipline.getId());
        setTitle(discipline.getTitle());
        setSportId(discipline.getTypeOfSportId());
    }
}
