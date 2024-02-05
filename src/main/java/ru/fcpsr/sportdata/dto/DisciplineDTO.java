package ru.fcpsr.sportdata.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class DisciplineDTO {
    @Id
    private int id;
    private String title;
    private TypeOfSportDTO typeOfSport;
    private List<AgeGroupDTO> ageGroupDTOS = new ArrayList<>();

    public void addAgeGroup(AgeGroupDTO ageGroupDTO) {
        ageGroupDTOS.add(ageGroupDTO);
    }
}
