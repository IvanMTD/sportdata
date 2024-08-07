package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.dto.ParticipantModelDTO;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/*
create table if not exists participant(
    id int GENERATED BY DEFAULT AS IDENTITY,
    name text,
    middle_name text,
    lastname text not null,
    birthday date not null,
    sport_school_ids integer[],
    qualification_ids integer[]
);
 */

@Data
@NoArgsConstructor
public class Participant {
    @Id
    private int id;
    private String name;
    private String middleName;
    private String lastname;
    private LocalDate birthday;
    private Set<Integer> sportSchoolIds = new HashSet<>();
    private Set<Integer> qualificationIds = new HashSet<>();

    public Participant(ParticipantDTO participantDTO) {
        setLastname(participantDTO.getLastname().trim());
        setName(participantDTO.getName().trim());
        setMiddleName(participantDTO.getMiddleName().trim());
        setBirthday(participantDTO.getBirthday());
    }

    public Participant(ParticipantModelDTO participant) {
        setLastname(participant.getLastname());
        setName(participant.getName());
        setMiddleName(participant.getMiddleName());
        setBirthday(participant.getBirthday());
    }

    public void addSportSchool(SportSchool sportSchool){
        sportSchoolIds.add(sportSchool.getId());
    }

    public void addSportSchoolId(int sportSchoolId){
        if(sportSchoolIds == null){
            sportSchoolIds = new HashSet<>();
        }
        sportSchoolIds.add(sportSchoolId);
    }

    public void addQualification(Qualification qualification){
        qualificationIds.add(qualification.getId());
    }

    public void addQualificationId(int qualificationId){
        qualificationIds.add(qualificationId);
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
}
