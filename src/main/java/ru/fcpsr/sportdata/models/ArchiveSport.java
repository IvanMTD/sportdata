package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.dto.SportDTO;

import java.util.HashSet;
import java.util.Set;

/*
create table if not exists archive_sport(
    id int GENERATED BY DEFAULT AS IDENTITY,
    contest_id int,
    type_of_sport_id int,
    discipline_id int,
    age_group_id int,
    place_ids integer[]
);
 */

@Data
@NoArgsConstructor
public class ArchiveSport {
    @Id
    private int id;
    private int contestId;
    private int typeOfSportId;
    private int disciplineId;
    private int ageGroupId;
    private Set<Integer> placeIds = new HashSet<>();

    public ArchiveSport(SportDTO sportDTO){
        setTypeOfSportId(sportDTO.getSportId());
        setDisciplineId(sportDTO.getDisciplineId());
        setAgeGroupId(sportDTO.getGroupId());
    }

    public void addPlace(Place place){
        placeIds.add(place.getId());
    }
}