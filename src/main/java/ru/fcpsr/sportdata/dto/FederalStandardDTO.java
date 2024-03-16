package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.FederalStandard;

@Data
@NoArgsConstructor
public class FederalStandardDTO {
    private FederalStandard federalStandard;
    private String title;
    public FederalStandardDTO(FederalStandard federalStandard){
        this.federalStandard = federalStandard;
        this.title = federalStandard.getTitle();
    }
}
