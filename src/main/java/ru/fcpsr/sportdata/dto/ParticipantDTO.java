package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.Subject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
public class ParticipantDTO {
    private int id;
    private String name;
    private String middleName;
    private String lastname;
    private LocalDate birthday;

    private List<SubjectDTO> subjects = new ArrayList<>();
    private List<QualificationDTO> qualifications = new ArrayList<>();

    public ParticipantDTO(Participant participant) {
        setId(participant.getId());
        if(participant.getLastname().equals("")){
            setLastname("");
        }else {
            setLastname(participant.getLastname());
        }
        if(participant.getName().equals("")){
            setName("");
        }else {
            setName(participant.getName());
        }
        if(participant.getMiddleName().equals("")){
            setMiddleName("");
        }else {
            setMiddleName(participant.getMiddleName());
        }
        setBirthday(participant.getBirthday());
    }

    public String getFullName(){
        return lastname + " " + name + " " + middleName;
    }

    public String getDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(birthday);
    }

    public int getAge(){
        return LocalDate.now().getYear() - birthday.getYear();
    }

    public void addSubject(SubjectDTO subjectDTO){
        subjects.add(subjectDTO);
    }

    public void addQualification(QualificationDTO qualificationDTO){
        qualifications.add(qualificationDTO);
    }
}
