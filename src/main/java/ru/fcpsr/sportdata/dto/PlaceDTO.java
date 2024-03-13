package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaceDTO {
    private int participantId;
    private ParticipantDTO participant;
    private int qualificationId;
    private QualificationDTO qualification;
    private int schoolId;
    private SportSchoolDTO school;
    private int place;
}
