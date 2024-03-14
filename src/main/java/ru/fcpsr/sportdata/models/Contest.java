package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.dto.ContestDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class Contest {
    @Id
    private int id;
    private int subjectId;
    private String ekp;
    private String title;
    private String city;
    private String location;
    private String contestRule;
    private String federalRule;
    private LocalDate beginning;
    private LocalDate ending;

    private Set<Integer> aSportIds = new HashSet<>();

    public Contest(ContestDTO contestDTO){
        setSubjectId(contestDTO.getSubjectId());
        setEkp(contestDTO.getEkp());
        setTitle(contestDTO.getTitle());
        setCity(contestDTO.getCity());
        setLocation(contestDTO.getLocation());
        setBeginning(contestDTO.getBeginning());
        setEnding(contestDTO.getEnding());
    }

    public void addArchiveSport(ArchiveSport archiveSport){
        aSportIds.add(archiveSport.getId());
    }

    public String getBeginningDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(beginning);
    }

    public String getEndingDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(ending);
    }
}
