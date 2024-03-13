package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class SportDTO {
    private int sportId;
    private TypeOfSportDTO sport;
    private int disciplineId;
    private DisciplineDTO discipline;
    private int groupId;
    private AgeGroupDTO group;
    List<PlaceDTO> places = new ArrayList<>();
}
