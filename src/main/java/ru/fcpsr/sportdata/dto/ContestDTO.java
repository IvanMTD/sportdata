package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Condition;
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
    private SubjectDTO subject;
    private int sportId;
    private TypeOfSportDTO sport;

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
    private int vrc;
    private int fc;
    private int sc;
    private int tc;
    private int bc;

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
        setVrc(contest.getVrc());
        setFc(contest.getFc());
        setSc(contest.getSc());
        setTc(contest.getTc());
        setBc(contest.getBc());

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

        /*for(SubjectDTO subject : subjects){
            int num = 0;
            for(SubjectDTO in : baseSubjectIn){
                if(subject.getId() == in.getId()){
                    num++;
                }
            }
            if(num == 0){
                noBaseSubjects.add(subject);
            }
        }*/

        baseSubjectOut = buildList(baseSubjectTotal);

        /*for(SubjectDTO total : baseSubjectTotal){
            int num = 0;
            for(SubjectDTO in : baseSubjectIn){
                if(in.getId() == total.getId()){
                    num++;
                }
            }
            if(num == 0){
                baseSubjectOut.add(total);
            }
        }*/

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

    public String getEVSK(){
        StringBuilder builder = new StringBuilder();
        if(yn3Date != 0){
            builder.append("Третий юношеский разряд возраст с ").append(yn3Date).append("|");
        }
        if(yn2Date != 0){
            builder.append("Второй юношеский разряд возраст с ").append(yn2Date).append("|");
        }
        if(yn1Date != 0){
            builder.append("Первый юношеский разряд возраст с ").append(yn1Date).append("|");
        }
        if(r3Date != 0){
            builder.append("Третий разряд возраст с ").append(r3Date).append("|");
        }
        if(r2Date != 0){
            builder.append("Второй разряд возраст с ").append(r2Date).append("|");
        }
        if(r1Date != 0){
            builder.append("Первый разряд возраст с ").append(r1Date).append("|");
        }
        if(kmsDate != 0){
            builder.append("Кандидаты в мастера спорта возраст с ").append(kmsDate).append("|");
        }
        if(msDate != 0){
            builder.append("Мастера спорта возраст с ").append(msDate).append("|");
        }
        if(msmkDate != 0){
            builder.append("Мастера спорта международного класса возраст с ").append(msmkDate).append("|");
        }
        if(zmsDate != 0){
            builder.append("Заслуженные мастера спорта возраст с ").append(zmsDate).append("|");
        }

        String string = builder.toString();
        String[] parts = string.split("\\|");
        StringBuilder answer = new StringBuilder();
        for(int i=0; i<parts.length; i++){
            if(i != parts.length - 1){
                answer.append(parts[i]).append(", ");
            }else{
                answer.append(parts[i]).append(". ");
            }
        }
        return answer.toString();
    }

    public List<String> getStatusStart(){
        int brCount = 0;
        int yn3Count = 0;
        int yn2Count = 0;
        int yn1Count = 0;
        int r3Count = 0;
        int r2Count = 0;
        int r1Count = 0;
        int kmsCount = 0;
        int msCount = 0;
        int msmkCount = 0;
        int zmsCount = 0;
        for(SportDTO discipline : sports){
            for(PlaceDTO place : discipline.getPlaces()){
                Category category = place.getQualification().getCategory();
                if(category.equals(Category.BR)){
                    brCount++;
                }else if(category.equals(Category.YN3)){
                    yn3Count++;
                }else if(category.equals(Category.YN2)){
                    yn2Count++;
                }else if(category.equals(Category.YN1)){
                    yn1Count++;
                }else if(category.equals(Category.R3)){
                    r3Count++;
                }else if(category.equals(Category.R2)){
                    r2Count++;
                }else if(category.equals(Category.R1)){
                    r1Count++;
                }else if(category.equals(Category.KMS)){
                    kmsCount++;
                }else if(category.equals(Category.MS)){
                    msCount++;
                }else if(category.equals(Category.MSMK)){
                    msmkCount++;
                }else if(category.equals(Category.ZMS)){
                    zmsCount++;
                }
            }
        }

        List<String> statusList = new ArrayList<>();

        if(brCount != 0){
            statusList.add("без разряда - " + brCount);
        }
        if(yn3Count != 0){
            statusList.add("юношеского третьего разряда - " + yn3Count);
        }
        if(yn2Count != 0){
            statusList.add("юношеского второго разряда - " + yn2Count);
        }
        if(yn1Count != 0){
            statusList.add("юношеского первого разряда - " + yn1Count);
        }
        if(r3Count != 0){
            statusList.add("третьего разряда - " + r3Count);
        }
        if(r2Count != 0){
            statusList.add("второго разряда - " + r2Count);
        }
        if(r1Count != 0){
            statusList.add("первого разряда - " + r1Count);
        }
        if(kmsCount != 0){
            statusList.add("кандидатов в мастера спорта - " + kmsCount);
        }
        if(msCount != 0){
            statusList.add("мастеров спорта - " + msCount);
        }
        if(msmkCount != 0){
            statusList.add("мастеров спорта международного класса - " + msmkCount);
        }
        if(zmsCount != 0){
            statusList.add("заслуженных мастеров спорта - " + zmsCount);
        }

        return statusList;
    }

    public List<String> getAllowedResult(){
        int brCount = 0;
        int brDone = 0;
        int brAllow = 0;
        int brNotAllow = 0;

        int yn3Count = 0;
        int yn3Done = 0;
        int yn3Allow = 0;
        int yn3NotAllow = 0;

        int yn2Count = 0;
        int yn2Done = 0;
        int yn2Allow = 0;
        int yn2NotAllow = 0;

        int yn1Count = 0;
        int yn1Done = 0;
        int yn1Allow = 0;
        int yn1NotAllow = 0;

        int r3Count = 0;
        int r3Done = 0;
        int r3Allow = 0;
        int r3NotAllow = 0;

        int r2Count = 0;
        int r2Done = 0;
        int r2Allow = 0;
        int r2NotAllow = 0;

        int r1Count = 0;
        int r1Done = 0;
        int r1Allow = 0;
        int r1NotAllow = 0;

        int kmsCount = 0;
        int kmsDone = 0;
        int kmsAllow = 0;
        int kmsNotAllow = 0;

        int msCount = 0;
        int msDone = 0;
        int msAllow = 0;
        int msNotAllow = 0;

        int msmkCount = 0;
        int msmkDone = 0;
        int msmkAllow = 0;
        int msmkNotAllow = 0;

        int zmsCount = 0;
        int zmsDone = 0;
        int zmsAllow = 0;
        int zmsNotAllow = 0;

        for(SportDTO discipline : sports){
            for(PlaceDTO place : discipline.getPlaces()){
                Category category = place.getQualification().getCategory();
                if(category.equals(Category.BR)){
                    brCount++;
                    if(place.getCondition().equals(Condition.DONE)){
                        brDone++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        brAllow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        brNotAllow++;
                    }
                }else if(category.equals(Category.YN3)){
                    yn3Count++;
                    if(place.getCondition().equals(Condition.DONE)){
                        yn3Done++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        yn3Allow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        yn3NotAllow++;
                    }
                }else if(category.equals(Category.YN2)){
                    yn2Count++;
                    if(place.getCondition().equals(Condition.DONE)){
                        yn2Done++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        yn2Allow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        yn2NotAllow++;
                    }
                }else if(category.equals(Category.YN1)){
                    yn1Count++;
                    if(place.getCondition().equals(Condition.DONE)){
                        yn1Done++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        yn1Allow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        yn1NotAllow++;
                    }
                }else if(category.equals(Category.R3)){
                    r3Count++;
                    if(place.getCondition().equals(Condition.DONE)){
                        r3Done++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        r3Allow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        r3NotAllow++;
                    }
                }else if(category.equals(Category.R2)){
                    r2Count++;
                    if(place.getCondition().equals(Condition.DONE)){
                        r2Done++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        r2Allow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        r2NotAllow++;
                    }
                }else if(category.equals(Category.R1)){
                    r1Count++;
                    if(place.getCondition().equals(Condition.DONE)){
                        r1Done++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        r1Allow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        r1NotAllow++;
                    }
                }else if(category.equals(Category.KMS)){
                    kmsCount++;
                    if(place.getCondition().equals(Condition.DONE)){
                        kmsDone++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        kmsAllow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        kmsNotAllow++;
                    }
                }else if(category.equals(Category.MS)){
                    msCount++;
                    if(place.getCondition().equals(Condition.DONE)){
                        msDone++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        msAllow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        msNotAllow++;
                    }
                }else if(category.equals(Category.MSMK)){
                    msmkCount++;
                    if(place.getCondition().equals(Condition.DONE)){
                        msmkDone++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        msmkAllow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        msmkNotAllow++;
                    }
                }else if(category.equals(Category.ZMS)){
                    zmsCount++;
                    if(place.getCondition().equals(Condition.DONE)){
                        zmsDone++;
                    }else if(place.getCondition().equals(Condition.ALLOW)){
                        zmsAllow++;
                    }else if(place.getCondition().equals(Condition.NO)){
                        zmsNotAllow++;
                    }
                }
            }
        }

        List<String> resultList = new ArrayList<>();

        if(brCount != 0){
            resultList.add("без разряда - " + brCount + " из них: " + (brDone != 0 ? "выполнили - " + brDone + " " : "") + (brAllow != 0 ? "подтвердили - " + brAllow + " " : "") + (brNotAllow != 0 ? "не подтвердили - " + brNotAllow + " " : ""));
        }
        if(yn3Count != 0){
            resultList.add("юношеского третьего разряда - " + yn3Count + " из них: " + (yn3Done != 0 ? "выполнили - " + yn3Done + " " : "") + (yn3Allow != 0 ? "подтвердили - " + yn3Allow + " " : "") + (yn3NotAllow != 0 ? "не подтвердили - " + yn3NotAllow + " " : ""));
        }
        if(yn2Count != 0){
            resultList.add("юношеского второго разряда - " + yn2Count + " из них: " + (yn2Done != 0 ? "выполнили - " + yn2Done + " " : "") + (yn2Allow != 0 ? "подтвердили - " + yn2Allow + " " : "") + (yn2NotAllow != 0 ? "не подтвердили - " + yn2NotAllow + " " : ""));
        }
        if(yn1Count != 0){
            resultList.add("юношеского первого разряда - " + yn1Count + " из них: " + (yn1Done != 0 ? "выполнили - " + yn1Done + " " : "") + (yn1Allow != 0 ? "подтвердили - " + yn1Allow + " " : "") + (yn1NotAllow != 0 ? "не подтвердили - " + yn1NotAllow + " " : ""));
        }
        if(r3Count != 0){
            resultList.add("третьего разряда - " + r3Count + " из них: " + (r3Done != 0 ? "выполнили - " + r3Done + " " : "") + (r3Allow != 0 ? "подтвердили - " + r3Allow + " " : "") + (r3NotAllow != 0 ? "не подтвердили - " + r3NotAllow + " " : ""));
        }
        if(r2Count != 0){
            resultList.add("второго разряда - " + r2Count + " из них: " + (r2Done != 0 ? "выполнили - " + r2Done + " " : "") + (r2Allow != 0 ? "подтвердили - " + r2Allow + " " : "") + (r2NotAllow != 0 ? "не подтвердили - " + r2NotAllow + " " : ""));
        }
        if(r1Count != 0){
            resultList.add("первого разряда - " + r1Count + " из них: " + (r1Done != 0 ? "выполнили - " + r1Done + " " : "") + (r1Allow != 0 ? "подтвердили - " + r1Allow + " " : "") + (r1NotAllow != 0 ? "не подтвердили - " + r1NotAllow + " " : ""));
        }
        if(kmsCount != 0){
            resultList.add("кандидатов в мастера спорта - " + kmsCount + " из них: " + (kmsDone != 0 ? "выполнили - " + kmsDone + " " : "") + (kmsAllow != 0 ? "подтвердили - " + kmsAllow + " " : "") + (kmsNotAllow != 0 ? "не подтвердили - " + kmsNotAllow + " " : ""));
        }
        if(msCount != 0){
            resultList.add("мастеров спорта - " + msCount + " из них: " + (msDone != 0 ? "выполнили - " + msDone + " " : "") + (msAllow != 0 ? "подтвердили - " + msAllow + " " : "") + (msNotAllow != 0 ? "не подтвердили - " + msNotAllow + " " : ""));
        }
        if(msmkCount != 0){
            resultList.add("мастеров спорта международного класса - " + msmkCount + " из них: " + (msmkDone != 0 ? "выполнили - " + msmkDone + " " : "") + (msmkAllow != 0 ? "подтвердили - " + msmkAllow + " " : "") + (msmkNotAllow != 0 ? "не подтвердили - " + msmkNotAllow + " " : ""));
        }
        if(zmsCount != 0){
            resultList.add("заслуженных мастеров спорта - " + zmsCount + " из них: " + (zmsDone != 0 ? "выполнили - " + zmsDone + " " : "") + (zmsAllow != 0 ? "подтвердили - " + zmsAllow + " " : "") + (zmsNotAllow != 0 ? "не подтвердили - " + zmsNotAllow + " " : ""));
        }

        return resultList;
    }

    public String getAgeControl(){
        int minimal = 100;
        int maximal = 0;
        int violation = 0;
        for(SportDTO discipline : sports){
            int minAge = discipline.getGroup().getMinAge();
            int maxAge = discipline.getGroup().getMaxAge();
            for(PlaceDTO place : discipline.getPlaces()){
                int age = place.getParticipant().getAge();
                if(age < minimal){
                    minimal = age;
                }
                if(age > maximal){
                    maximal = age;
                }
                if(minAge > age && age > maxAge){
                    violation++;
                }
            }
        }

        if(violation == 0){
            return "от " + minimal + " до " + maximal + "(" + (LocalDate.now().getYear() - maximal) + "-" + (LocalDate.now().getYear() - minimal) + " годов рождения), что соответствует требованиям Положения о спортивных соревнованиях.";
        }else{
            return "от " + minimal + " до " + maximal + " (" + (LocalDate.now().getYear() - maximal) + "-" + (LocalDate.now().getYear() - minimal) + " годов рождения), что не соответствует требованиям Положения о спортивных соревнованиях.";
        }
    }
}
