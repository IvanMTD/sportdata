package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.Subject;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Data
@NoArgsConstructor
public class ParticipantDTO {
    private int id;
    private int oldSubjectId;
    private int subjectId;
    private int oldSchoolId;
    private int schoolId;
    private int qualificationId;
    private int sportId;
    @NotBlank(message = "Поле Имя обязательно для заполнения!")
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Имя должно начинаться с большой буквы все остальные символы маленькие!")
    private String name;
    private String middleName;
    @NotBlank(message = "Поле Фамилия обязательны для заполнения!")
    @Pattern(regexp = "^[А-Я][а-я]+", message = "Фамилия должно начинаться с большой буквы все остальные символы маленькие!")
    private String lastname;
    @Past(message = "Дата рождения не может быть текущей или будущей датой!")
    @NotNull(message = "Дата рождения обязательна! Укажите дату.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private String fullName;

    private Set<Integer> schoolIds = new HashSet<>();
    private List<SubjectDTO> subjects = new ArrayList<>();
    private List<SportSchoolDTO> schools = new ArrayList<>();
    private List<QualificationDTO> qualifications = new ArrayList<>();

    public ParticipantDTO(Participant participant) {
        if(participant.getLastname() == null){
            setId(0);
            setLastname("");
            setName("");
            setMiddleName("");
            setBirthday(LocalDate.now());
        }else{
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
    }

    public String getFullName(){
        return lastname + " " + name + " " + middleName;
    }

    public String getDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(birthday);
    }

    public int getAge(){
        Period age = Period.between(birthday, LocalDate.now());
        return age.getYears();
    }

    public void addSubject(SubjectDTO subjectDTO){
        subjects.add(subjectDTO);
    }

    public void addQualification(QualificationDTO qualificationDTO){
        qualifications.add(qualificationDTO);
    }
}
