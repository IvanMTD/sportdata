package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.dto.SportDTO;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.FederalStandard;

import java.util.*;

@Data
@NoArgsConstructor
public class ArchiveSport {
    @Id
    private int id;
    private long contestId;
    private int disciplineId;
    private int ageGroupId;
    private Set<FederalStandard> federalStandards = new HashSet<>();
    private Set<Category> allowed = new HashSet<>();
    private Set<Integer> placeIds = new HashSet<>();

    public ArchiveSport(SportDTO sportDTO){
        setDisciplineId(sportDTO.getDisciplineId());
        setAgeGroupId(sportDTO.getGroupId());
        for(FederalStandard federalStandard : sportDTO.getStandards()){
            if(federalStandard != null){
                federalStandards.add(federalStandard);
            }
        }
        for(Category category : sportDTO.getAllowed()){
            if(category != null){
                allowed.add(category);
            }
        }
    }

    public void setupAllowed(List<Category> categories){
        allowed = new HashSet<>();
        for(Category category : categories){
            if(category != null){
                allowed.add(category);
            }
        }
    }

    public void setupFederalStandards(List<FederalStandard> fs){
        federalStandards = new HashSet<>();
        for(FederalStandard federalStandard : fs){
            if(federalStandard != null){
                federalStandards.add(federalStandard);
            }
        }
    }

    public void addPlace(Place place){
        placeIds.add(place.getId());
    }
    public void addAllow(Category category){
        allowed.add(category);
    }
}
