package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Condition;
import ru.fcpsr.sportdata.models.Place;

@Data
@NoArgsConstructor
public class PlaceDTO {
    private int id;
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

    public boolean myEquals(PlaceDTO placeDTO){
        return participantId == placeDTO.getParticipantId();
    }
}
