package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.Condition;
import ru.fcpsr.sportdata.models.Contest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ContestDTO {
    private long id;
    @NotBlank(message = "Поле не может быть пустым! ЕКП обязателен!")
    private String ekp;
    @NotBlank(message = "Поле не может быть пустым! Укажите название!")
    private String title;
    @NotBlank(message = "Укажите город проведения соревнования!")
    private String city;
    @NotBlank(message = "Укажите спортивный объект проведения соревнования!")
    private String location;
    private int subjectId;
    private SubjectDTO subject;
    private int sportId;
    private TypeOfSportDTO sport;
    private String subjectTitle;
    private String sportTitle;

    private List<Integer> totalSubjects = new ArrayList<>(Collections.nCopies(100, null));
    private List<SubjectDTO> subjects = new ArrayList<>();
    private List<SubjectDTO> baseSubjectTotal = new ArrayList<>();
    private List<SubjectDTO> baseSubjectIn = new ArrayList<>();
    private List<SubjectDTO> baseSubjectOut = new ArrayList<>();
    private List<SubjectDTO> noBaseSubjects = new ArrayList<>();

    private List<Integer> firstPlace = new ArrayList<>(Collections.nCopies(10, null));
    private List<SubjectDTO> firstSubjects = new ArrayList<>();
    private List<Integer> secondPlace = new ArrayList<>(Collections.nCopies(10, null));
    private List<SubjectDTO> secondSubjects = new ArrayList<>();
    private List<Integer> lastPlace = new ArrayList<>(Collections.nCopies(10, null));
    private List<SubjectDTO> lastSubjects = new ArrayList<>();

    private int participantTotal;
    private int boyTotal;
    private int girlTotal;

    // total
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

    boolean complete;

    @NotNull(message = "Укажите начало соревнования")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginning;
    @NotNull(message = "Укажите завершение соревнования")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ending;
    private List<SportDTO> sports = new ArrayList<>();

    public ContestDTO(Contest contest) {
        setId(contest.getId());
        setEkp(contest.getEkp());
        setTitle(contest.getTitle());
        setCity(contest.getCity());
        setLocation(contest.getLocation());
        setSubjectTitle(contest.getSubjectTitle());
        setSportTitle(contest.getSportTitle());

        setSubjectId(contest.getSubjectId());
        setSportId(contest.getTypeOfSportId());

        totalSubjects.addAll(contest.getTotalSubjects());
        firstPlace.addAll(contest.getFirstPlace());
        secondPlace.addAll(contest.getSecondPlace());
        lastPlace.addAll(contest.getLastPlace());

        setBeginning(contest.getBeginning());
        setEnding(contest.getEnding());

        setBr(contest.getBr());
        setYn1(contest.getYn1());
        setYn2(contest.getYn2());
        setYn3(contest.getYn3());
        setR1(contest.getR1());
        setR2(contest.getR2());
        setR3(contest.getR3());
        setKms(contest.getKms());
        setMs(contest.getMs());
        setMsmk(contest.getMsmk());
        setZms(contest.getZms());

        setTrainerTotal(contest.getTrainerTotal());
        setJudgeTotal(contest.getJudgeTotal());
        setNonresidentJudge(contest.getNonresidentJudge());
        setMc(contest.getMc());
        setVrc(contest.getVrc());
        setFc(contest.getFc());
        setSc(contest.getSc());
        setTc(contest.getTc());
        setBc(contest.getBc());
        setDc(contest.getDc());

        setYn1Date(contest.getYn1Date());
        setYn2Date(contest.getYn2Date());
        setYn3Date(contest.getYn3Date());
        setR1Date(contest.getR1Date());
        setR2Date(contest.getR2Date());
        setR3Date(contest.getR3Date());
        setKmsDate(contest.getKmsDate());
        setMsDate(contest.getMsDate());
        setMsmkDate(contest.getMsmkDate());
        setZmsDate(contest.getZmsDate());

        setParticipantTotal(contest.getParticipantTotal());
        setBoyTotal(contest.getBoyTotal());
        setGirlTotal(contest.getGirlTotal());

        setComplete(contest.isComplete());
    }

    public void addSubjectInTotal(SubjectDTO subject){
        subjects.add(subject);
    }
    public void addSubjectInFirst(SubjectDTO subject){
        firstSubjects.add(subject);
    }
    public void addSubjectInSecond(SubjectDTO subject){
        secondSubjects.add(subject);
    }
    public void addSubjectInLast(SubjectDTO subject){
        lastSubjects.add(subject);
    }
    public void addSubjectInBaseTotal(SubjectDTO subject){
        baseSubjectTotal.add(subject);
    }
    public void addSubjectInBaseIn(SubjectDTO subject){
        baseSubjectIn.add(subject);
    }

    public void calcBaseIn(){
        for(SubjectDTO subject : subjects){
            for(SubjectDTO base : baseSubjectTotal){
                if(subject.getId() == base.getId()){
                    baseSubjectIn.add(subject);
                }
            }
        }

        noBaseSubjects = buildList(subjects);

        baseSubjectOut = buildList(baseSubjectTotal);

        subjects = subjects.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
        baseSubjectIn = baseSubjectIn.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
        baseSubjectOut = baseSubjectOut.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
        baseSubjectTotal = baseSubjectTotal.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
    }

    private List<SubjectDTO> buildList(List<SubjectDTO> list){
        List<SubjectDTO> subjectList = new ArrayList<>();
        for(SubjectDTO subject : list){
            int num = 0;
            for(SubjectDTO subject2 : baseSubjectIn){
                if(subject2.getId() == subject.getId()){
                    num++;
                }
            }
            if(num == 0){
                subjectList.add(subject);
            }
        }
        return subjectList;
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

    public int getContestYear(){
        return beginning.getYear();
    }
}
