package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParticipantContestDTO {
    private String ekpId;
    private String ekpNum;
    private String ekpTitle;
    private String date;
    private String sportTitle;
    private String disciplineTitle;
    private int place;
    private QualificationDTO q;
    private QualificationDTO nq;
}
