package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.enums.Condition;

@Data
@NoArgsConstructor
public class ConditionDTO {
    private Condition condition;
    private String title;

    public ConditionDTO(Condition condition){
        this.condition = condition;
        this.title = condition.getTitle();
    }
}
