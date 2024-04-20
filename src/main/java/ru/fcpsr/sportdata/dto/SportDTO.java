package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.FederalStandard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SportDTO {
    private int id;

    private int disciplineId;
    private DisciplineDTO discipline;
    private int groupId;
    private AgeGroupDTO group;
    private List<Category> allowed = new ArrayList<>(Collections.nCopies(Category.values().length, null));
    private List<CategoryDTO> allowedList = new ArrayList<>();
    private List<FederalStandard> standards = new ArrayList<>(Collections.nCopies(FederalStandard.values().length, null));
    private List<FederalStandardDTO> standardList = new ArrayList<>();
    List<PlaceDTO> places = new ArrayList<>();

    public SportDTO(ArchiveSport sport){
        setId(sport.getId());

        setDisciplineId(sport.getDisciplineId());
        setGroupId(sport.getAgeGroupId());

        if(sport.getAllowed() != null) {
            allowed.addAll(sport.getAllowed());
            setAllowedList(allowed);
        }
        if(sport.getFederalStandards() != null) {
            standards.addAll(sport.getFederalStandards());
            setStandardList(standards);
        }
    }

    public void setAllowedList(List<Category> allowed) {
        for(Category category : allowed){
            if(category != null){
                CategoryDTO categoryDTO = new CategoryDTO(category);
                allowedList.add(categoryDTO);
            }
        }
        allowedList = allowedList.stream().sorted(Comparator.comparing(CategoryDTO::getTitle)).collect(Collectors.toList());
    }

    public void setStandardList(List<FederalStandard> fs) {
        for(FederalStandard federalStandard : fs){
            if(federalStandard != null){
                FederalStandardDTO federalStandardDTO = new FederalStandardDTO(federalStandard);
                standardList.add(federalStandardDTO);
            }
        }
        standardList = standardList.stream().sorted(Comparator.comparing(FederalStandardDTO::getTitle)).collect(Collectors.toList());
    }
}
