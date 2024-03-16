package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.FederalStandard;

import java.util.ArrayList;
import java.util.Collections;
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
    private List<Category> allowed = new ArrayList<>(Collections.nCopies(Category.values().length, null));
    private FederalStandard standard;
    List<PlaceDTO> places = new ArrayList<>();
}
