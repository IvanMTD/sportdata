package ru.fcpsr.sportdata.dto;

import lombok.Data;
import ru.fcpsr.sportdata.models.FederalDistrict;
import ru.fcpsr.sportdata.models.Subject;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubjectDTO {
    private int id;
    private String title;
    private FederalDistrict federalDistrict;
    private List<TypeOfSportDTO> typeOfSportDTOS = new ArrayList<>();

    public SubjectDTO(Subject subject){
        setId(subject.getId());
        setTitle(subject.getTitle());
        setFederalDistrict(subject.getFederalDistrict());
    }
}
