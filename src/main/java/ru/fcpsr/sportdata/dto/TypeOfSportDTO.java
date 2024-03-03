package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TypeOfSportDTO {
    private int id;
    @NotBlank(message = "Вы пытались добавить/обновить название вида спорта с пустым полем. Поле не может быть пустым!")
    private String title;
    private Season season;
    private SportFilterType sportFilterType;
    List<SubjectDTO> subjects = new ArrayList<>();
    List<DisciplineDTO> disciplines = new ArrayList<>();

    public TypeOfSportDTO(TypeOfSport sport){
        setId(sport.getId());
        setTitle(sport.getTitle());
        if(sport.getSeason() == null){
            setSeason(Season.NO);
        }else{
            setSeason(sport.getSeason());
        }
        if(sport.getSportFilterType() == null){
            setSportFilterType(SportFilterType.NO);
        }else{
            setSportFilterType(sport.getSportFilterType());
        }
    }
}
