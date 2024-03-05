package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubSportPartDTO {
    private int sportId;
    private int subjectId;
    private int participantId;
    private String fullName;
}
