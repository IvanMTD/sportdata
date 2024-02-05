package ru.fcpsr.sportdata.dto;

import lombok.Data;
import ru.fcpsr.sportdata.models.*;

import java.util.ArrayList;
import java.util.List;

@Data
public class TypeOfSportDTO {
    private int id;
    private String title;
    private Season season;
    private SportFilterType sportFilterType;
    List<SubjectDTO> subjectIds = new ArrayList<>();
    List<DisciplineDTO> disciplines = new ArrayList<>();

    public TypeOfSportDTO(TypeOfSport sport){
        setId(sport.getId());
        setTitle(sport.getTitle());
        setSeason(sport.getSeason());
        setSportFilterType(sport.getSportFilterType());
    }
}
