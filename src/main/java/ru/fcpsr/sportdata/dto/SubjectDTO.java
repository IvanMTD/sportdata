package ru.fcpsr.sportdata.dto;

import lombok.Data;
import ru.fcpsr.sportdata.models.FederalDistrict;

import java.util.ArrayList;
import java.util.List;

@Data
public class SubjectDTO {
    private int id;
    private String title;
    private FederalDistrict federalDistrict;
    private List<TypeOfSportDTO> typeOfSportDTOS = new ArrayList<>();
}
