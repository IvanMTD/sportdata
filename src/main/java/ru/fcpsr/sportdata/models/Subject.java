package ru.fcpsr.sportdata.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.enums.FederalDistrict;

import java.util.HashSet;
import java.util.Set;

/*
create table if not exists subject(
    id int GENERATED BY DEFAULT AS IDENTITY,
    title text not null,
    iso text not null,
    federal_district text not null,
    sport_school_ids integer[],
    base_sport_ids integer[]
);
 */

@Data
public class Subject {
    @Id
    private int id;
    private String title;
    private String iso;
    private FederalDistrict federalDistrict;
    private Set<Integer> sportSchoolIds = new HashSet<>();
    private Set<Integer> baseSportIds = new HashSet<>();

    public void addSportSchool(SportSchool sportSchool){
        sportSchoolIds.add(sportSchool.getId());
    }

    public void addSportSchoolId(int sportSchoolId){
        sportSchoolIds.add(sportSchoolId);
    }

    public void addBaseSport(BaseSport baseSport){
        if(baseSportIds == null){
            baseSportIds = new HashSet<>();
        }
        baseSportIds.add(baseSport.getId());
    }

    public void addBaseSportId(int baseSportId){
        baseSportIds.add(baseSportId);
    }
}
