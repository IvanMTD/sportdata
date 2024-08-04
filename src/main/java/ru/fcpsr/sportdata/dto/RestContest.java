package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RestContest {
    private List<ContestDTO> contests = new ArrayList<>();
    private List<ContestDTO> olympic = new ArrayList<>();
    private List<ContestDTO> noOlympic = new ArrayList<>();
    private List<ContestDTO> adaptive = new ArrayList<>();
    private long total;
}
