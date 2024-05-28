package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ParticipantContestDTO {
    private int contestId;
    private String ekpNum;
    private String contestTitle;
    private String date;
    private String sportTitle;
    private String disciplineTitle;
    private int place;
    private CategoryDTO mainCategory;
    private ConditionDTO condition;
    private CategoryDTO newCategory;
    private LocalDate localDate;
}
