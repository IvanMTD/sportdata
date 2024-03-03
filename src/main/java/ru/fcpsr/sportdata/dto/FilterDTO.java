package ru.fcpsr.sportdata.dto;

import lombok.Data;
import ru.fcpsr.sportdata.models.Season;
import ru.fcpsr.sportdata.models.SportFilterType;

@Data
public class FilterDTO {
    private int sportId;
    private Season season;
    private SportFilterType filter;
}
