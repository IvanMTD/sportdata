package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.FederalStandard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class SportDTO {
    private int id;

    private int disciplineId;
    private DisciplineDTO discipline;
    private int groupId;
    private AgeGroupDTO group;
    private List<Category> allowed = new ArrayList<>(Collections.nCopies(Category.values().length, null));
    private List<FederalStandard> standards = new ArrayList<>(Collections.nCopies(FederalStandard.values().length, null));
    List<PlaceDTO> places = new ArrayList<>();

    public SportDTO(ArchiveSport sport){
        setId(sport.getId());

        setDisciplineId(sport.getDisciplineId());
        setGroupId(sport.getAgeGroupId());

        if(sport.getAllowed() != null) {
            allowed.addAll(sport.getAllowed());
        }
        if(sport.getFederalStandards() != null) {
            standards.addAll(sport.getFederalStandards());
        }
    }
}
