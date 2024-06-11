package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.Condition;
import ru.fcpsr.sportdata.models.Place;

import java.util.Objects;

@Data
@NoArgsConstructor
public class PlaceDTO {
    private long id;
    private int participantId;
    private ParticipantDTO participant;
    private int qualificationId;
    private QualificationDTO qualification;
    private Category newQualificationData;
    private QualificationDTO newQualification;
    private int schoolId;
    private SportSchoolDTO school;
    private int place;
    private Condition condition;

    private String info;
    private int parallelSchoolId;
    private SportSchoolDTO parallelSchool;

    public PlaceDTO(Place place){
        setId(place.getId());
        setParticipantId(place.getParticipantId());
        setQualificationId(place.getQualificationId());
        setSchoolId(place.getSportSchoolId());
        setPlace(place.getPlace());
        setNewQualificationData(place.getResultCategory());

        setParallelSchoolId(place.getParallelSchoolId());
        setInfo(place.getInfo());

        if(place.getCondition() != null) {
            setCondition(place.getCondition());
        }
    }

    public Category getNewQualificationData() {
        return Objects.requireNonNullElse(newQualificationData, Category.NO);
    }

    public Condition getCondition() {
        return Objects.requireNonNullElse(condition, Condition.NO);
    }
}
