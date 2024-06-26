package ru.fcpsr.sportdata.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.enums.Season;
import ru.fcpsr.sportdata.enums.SportFilterType;

import java.util.HashSet;
import java.util.Set;

/*
create table if not exists type_of_sport(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    season text,
    sport_filter_type text,
    base_sport_ids integer[],
    discipline_ids integer[],
    age_group_ids integer[],
    qualification_ids integer[]
);
 */

@Data
public class TypeOfSport {
    @Id
    private int id;
    private String title;
    private Season season;
    private SportFilterType sportFilterType;
    private Set<Integer> baseSportIds = new HashSet<>();
    private Set<Integer> disciplineIds = new HashSet<>();
    private Set<Integer> ageGroupIds = new HashSet<>();
    private Set<Integer> qualificationIds = new HashSet<>();

    public void addBaseSport(BaseSport baseSport){
        if(baseSportIds == null){
            baseSportIds = new HashSet<>();
        }
        baseSportIds.add(baseSport.getId());
    }

    public void addBaseSportId(int baseSportId){
        baseSportIds.add(baseSportId);
    }

    public void addDiscipline(Discipline discipline){
        disciplineIds.add(discipline.getId());
    }

    public void addDisciplineId(int disciplineId){
        disciplineIds.add(disciplineId);
    }

    public void addAgeGroup(AgeGroup ageGroup){
        ageGroupIds.add(ageGroup.getId());
    }

    public void addAgeGroupId(int ageGroupId){
        ageGroupIds.add(ageGroupId);
    }

    public void addQualification(Qualification qualification){
        qualificationIds.add(qualification.getId());
    }

    public void addQualificationId(int qualificationId){
        qualificationIds.add(qualificationId);
    }
}
