package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Qualification;

import java.util.Objects;

@Data
@NoArgsConstructor
public class QualificationDTO {
    private int id;
    private int sportId;
    private int participantId;
    private Category category;
    private String categoryTitle;
    private TypeOfSportDTO sport;
    private ParticipantDTO participant;

    public QualificationDTO(Qualification qualification) {
        setId(qualification.getId());
        if(qualification.getCategory() != null) {
            setCategory(qualification.getCategory());
        }
    }

    public void setCategory(Category category){
        this.category = category;
        this.categoryTitle = category.getTitle();
    }

    public Category getCategory() {
        return Objects.requireNonNullElse(category, Category.NO);
    }
}
