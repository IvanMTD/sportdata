package ru.fcpsr.sportdata.dto;

import lombok.Data;
import ru.fcpsr.sportdata.enums.Season;
import ru.fcpsr.sportdata.enums.SportFilterType;

@Data
public class FilterDTO {
    private int sportId;
    private Season season;
    private SportFilterType filter;
}
