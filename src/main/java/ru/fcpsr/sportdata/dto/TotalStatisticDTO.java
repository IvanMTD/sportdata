package ru.fcpsr.sportdata.dto;

import lombok.Data;

@Data
public class TotalStatisticDTO {
    private int sportTotal;
    private int disciplineTotal;
    private int groupTotal;
    private int subjectTotal;
    private int schoolTotal;
    private int participantTotal;
    private int qualificationTotal;

    private int baseSportTotal;
    private int contestTotal;
}
