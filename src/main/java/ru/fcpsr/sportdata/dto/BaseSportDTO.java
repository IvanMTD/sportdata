package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.BaseSport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class BaseSportDTO {
    private int id;
    private SubjectDTO subject;
    private TypeOfSportDTO sport;

    private int subjectId;
    private int sportId;
    @NotNull(message = "Укажите дату выдачи базового спорта субъекту!")
    @Past(message = "Дата начала не может быть текущей или датой из будущего!")
    private LocalDate issueDate;
    private int expiration;
    private boolean issueDateError;

    public BaseSportDTO(BaseSport baseSport) {
        setId(baseSport.getId());
        setIssueDate(baseSport.getIssueDate());
        setExpiration(baseSport.getExpiration());
        int year = LocalDate.now().getYear();
        issueDateError = expiration <= year;
    }

    public String getDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(issueDate);
    }

    public int getDateYear(){
        return issueDate.getYear();
    }
}
