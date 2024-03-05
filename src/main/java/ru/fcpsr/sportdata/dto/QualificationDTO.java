package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Qualification;

@Data
@NoArgsConstructor
public class QualificationDTO {
    private int id;
    private Category category;
    private AgeGroupDTO group;
    private ParticipantDTO participant;

    public QualificationDTO(Qualification qualification) {
        setId(qualification.getId());
        if(qualification.getCategory() != null) {
            setCategory(qualification.getCategory());
        }
    }
}
