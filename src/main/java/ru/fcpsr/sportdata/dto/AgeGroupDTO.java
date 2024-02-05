package ru.fcpsr.sportdata.dto;

import lombok.Data;


@Data
public class AgeGroupDTO {
    private int id;
    private String title;
    private int minAge;
    private int maxAge;
    private DisciplineDTO discipline;
}
