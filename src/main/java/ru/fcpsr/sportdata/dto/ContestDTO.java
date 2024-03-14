package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.fcpsr.sportdata.models.Contest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ContestDTO {
    private int id;
    @NotBlank(message = "Поле не может быть пустым! ЕКП обязателен!")
    private String ekp;
    @NotBlank(message = "Поле не может быть пустым! Укажите название!")
    private String title;
    @NotBlank(message = "Укажите город проведения соревнования!")
    private String city;
    @NotBlank(message = "Укажите спортивный объект проведения соревнования!")
    private String location;
    private int subjectId;
    @NotNull(message = "Укажите начало соревнования")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginning;
    @NotNull(message = "Укажите завершение соревнования")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ending;
    private SubjectDTO subject;
    private List<SportDTO> sports = new ArrayList<>();

    public ContestDTO(Contest contest) {
        setId(contest.getId());
        setEkp(contest.getEkp());
        setTitle(contest.getTitle());
        setCity(contest.getCity());
        setLocation(contest.getLocation());
        setBeginning(contest.getBeginning());
        setEnding(contest.getEnding());
    }

    public void addSport(SportDTO sportDTO){
        if(sports == null){
            sports = new ArrayList<>();
        }
        sports.add(sportDTO);
    }

    public String getBeginningDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(beginning);
    }

    public String getEndingDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(ending);
    }
}
