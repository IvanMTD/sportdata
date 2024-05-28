package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.dto.PlaceDTO;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.Condition;

/*
create table if not exists place(
    id int GENERATED BY DEFAULT AS IDENTITY,
    a_sport_id int,
    participant_id int,
    qualification_id int,
    sport_school_id int,
    place int
);
 */

@Data
@NoArgsConstructor
public class Place {
    @Id
    private int id;
    private int aSportId;
    private int participantId;
    private int qualificationId;
    private int newQualificationId;
    private int sportSchoolId;
    private int place;
    private Condition condition;
    private Category resultCategory;

    private int parallelSchoolId;
    private String info;

    public Place(PlaceDTO placeDTO){
        setParticipantId(placeDTO.getParticipantId());
        setQualificationId(placeDTO.getQualificationId());
        setSportSchoolId(placeDTO.getSchoolId());
        setPlace(placeDTO.getPlace());
        setResultCategory(placeDTO.getNewQualificationData());
        setCondition(placeDTO.getCondition());
        setInfo(placeDTO.getInfo());
        setParallelSchoolId(placeDTO.getParallelSchoolId());
    }
}
