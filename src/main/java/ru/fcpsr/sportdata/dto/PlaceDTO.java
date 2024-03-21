package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Condition;

@Data
@NoArgsConstructor
public class PlaceDTO {
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
}
