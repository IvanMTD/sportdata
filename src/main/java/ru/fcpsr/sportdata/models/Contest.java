package ru.fcpsr.sportdata.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.fcpsr.sportdata.dto.ContestDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Data
@NoArgsConstructor
public class Contest {
    @Id
    private long id;
    private int subjectId;
    private int typeOfSportId;
    private String subjectTitle;
    private String sportTitle;
    private String ekp;
    private String title;
    private String city;
    private String location;
    private String contestRule;
    private String federalRule;
    private LocalDate beginning;
    private LocalDate ending;

    private Set<Integer> totalSubjects = new HashSet<>();
    private Set<Integer> firstPlace = new HashSet<>();
    private Set<Integer> secondPlace = new HashSet<>();
    private Set<Integer> lastPlace = new HashSet<>();

    private int participantTotal;
    private int boyTotal;
    private int girlTotal;

    private int br;
    private int yn1;
    private int yn2;
    private int yn3;
    private int r1;
    private int r2;
    private int r3;
    private int kms;
    private int ms;
    private int msmk;
    private int zms;

    private int trainerTotal;
    private int judgeTotal;
    private int nonresidentJudge;
    private int mc;
    private int vrc;
    private int fc;
    private int sc;
    private int tc;
    private int bc;
    private int dc;

    private int yn1Date;
    private int yn2Date;
    private int yn3Date;
    private int r1Date;
    private int r2Date;
    private int r3Date;
    private int kmsDate;
    private int msDate;
    private int msmkDate;
    private int zmsDate;

    private Set<Long> aSportIds = new HashSet<>();

    boolean complete;

    public Contest(ContestDTO contestDTO){
        setSubjectId(contestDTO.getSubjectId());
        setTypeOfSportId(contestDTO.getSportId());
        setEkp(contestDTO.getEkp());
        setTitle(contestDTO.getTitle());
        setCity(contestDTO.getCity());
        setLocation(contestDTO.getLocation());
        setBeginning(contestDTO.getBeginning());
        setEnding(contestDTO.getEnding());
        setSubjectTitle(contestDTO.getSubjectTitle());
        setSportTitle(contestDTO.getSportTitle());
        for(Integer subjectId : contestDTO.getTotalSubjects()){
            if(subjectId != null){
                totalSubjects.add(subjectId);
            }
        }
        for(Integer id : contestDTO.getFirstPlace()){
            if(id != null){
                firstPlace.add(id);
            }
        }
        for(Integer id : contestDTO.getSecondPlace()){
            if(id != null){
                secondPlace.add(id);
            }
        }
        for(Integer id : contestDTO.getLastPlace()){
            if(id != null){
                lastPlace.add(id);
            }
        }

        setParticipantTotal(contestDTO.getParticipantTotal());
        setBoyTotal(contestDTO.getBoyTotal());
        setGirlTotal(contestDTO.getGirlTotal());

        setYn1(contestDTO.getYn1());
        setYn2(contestDTO.getYn2());
        setYn3(contestDTO.getYn3());
        setR1(contestDTO.getR1());
        setR2(contestDTO.getR2());
        setR3(contestDTO.getR3());
        setKms(contestDTO.getKms());
        setMs(contestDTO.getMs());
        setMsmk(contestDTO.getMsmk());
        setZms(contestDTO.getZms());
        setBr(contestDTO.getBr());

        setTrainerTotal(contestDTO.getTrainerTotal());
        setJudgeTotal(contestDTO.getJudgeTotal());
        setNonresidentJudge(contestDTO.getNonresidentJudge());
        setMc(contestDTO.getMc());
        setVrc(contestDTO.getVrc());
        setFc(contestDTO.getFc());
        setSc(contestDTO.getSc());
        setTc(contestDTO.getTc());
        setBc(contestDTO.getBc());
        setDc(contestDTO.getDc());

        setYn1Date(contestDTO.getYn1Date());
        setYn2Date(contestDTO.getYn2Date());
        setYn3Date(contestDTO.getYn3Date());
        setR1Date(contestDTO.getR1Date());
        setR2Date(contestDTO.getR2Date());
        setR3Date(contestDTO.getR3Date());
        setKmsDate(contestDTO.getKmsDate());
        setMsDate(contestDTO.getMsDate());
        setMsmkDate(contestDTO.getMsmkDate());
        setZmsDate(contestDTO.getZmsDate());

        setComplete(contestDTO.isComplete());
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

    public void replaceSubjectIds(ContestDTO contestDTO) {
        totalSubjects = replaceData(contestDTO.getTotalSubjects());
    }

    public Set<Integer> replaceData(List<Integer> ids){
        Set<Integer> setIds = new HashSet<>();
        for(Integer id : ids){
            if(id != null){
                setIds.add(id);
            }
        }
        return setIds;
    }

    public void updateContestData(ContestDTO contestDTO) {

        setParticipantTotal(contestDTO.getParticipantTotal());
        setBoyTotal(contestDTO.getBoyTotal());
        setGirlTotal(contestDTO.getGirlTotal());

        setBr(contestDTO.getBr());
        setYn1(contestDTO.getYn1());
        setYn2(contestDTO.getYn2());
        setYn3(contestDTO.getYn3());
        setR1(contestDTO.getR1());
        setR2(contestDTO.getR2());
        setR3(contestDTO.getR3());
        setKms(contestDTO.getKms());
        setMs(contestDTO.getMs());
        setMsmk(contestDTO.getMsmk());
        setZms(contestDTO.getZms());

        setTrainerTotal(contestDTO.getTrainerTotal());
        setJudgeTotal(contestDTO.getJudgeTotal());
        setNonresidentJudge(contestDTO.getNonresidentJudge());
        setMc(contestDTO.getMc());
        setVrc(contestDTO.getVrc());
        setFc(contestDTO.getFc());
        setSc(contestDTO.getSc());
        setTc(contestDTO.getTc());
        setBc(contestDTO.getBc());
        setDc(contestDTO.getDc());

        setYn1Date(contestDTO.getYn1Date());
        setYn2Date(contestDTO.getYn2Date());
        setYn3Date(contestDTO.getYn3Date());
        setR1Date(contestDTO.getR1Date());
        setR2Date(contestDTO.getR2Date());
        setR3Date(contestDTO.getR3Date());
        setKmsDate(contestDTO.getKmsDate());
        setMsDate(contestDTO.getMsDate());
        setMsmkDate(contestDTO.getMsmkDate());
        setZmsDate(contestDTO.getZmsDate());

        this.firstPlace = replaceData(contestDTO.getFirstPlace());
        this.secondPlace = replaceData(contestDTO.getSecondPlace());
        this.lastPlace = replaceData(contestDTO.getLastPlace());
    }
}
