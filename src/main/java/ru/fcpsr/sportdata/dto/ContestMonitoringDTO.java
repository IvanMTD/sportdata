package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.fcpsr.sportdata.models.FederalStandard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class ContestMonitoringDTO {
    private String contestTitle;
    private String sportTitle;
    private String ekp;
    private String subjectTitle;
    private String city;
    private String location;
    private LocalDate beginning;
    private LocalDate ending;

    private int athletesTookPart;
    private int athletesTookPartBoy;
    private int athletesTookPartGirl;

    private int trainerTotal;
    private int judgeTotal;
    private int nonresidentJudge;
    private int mc;
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

    private List<SportDTO> disciplines = new ArrayList<>();
    private SubjectMonitoringDTO subjectMonitoring;

    public String getFormatContestTitle(){
        String[] words = contestTitle.split("\\s");
        StringBuilder newContestTitle = new StringBuilder();
        for(String word : words){
            if(word.toLowerCase().contains("росс") || word.toLowerCase().contains("федер")){
                newContestTitle.append(word).append(" ");
            }else{
                newContestTitle.append(word.toLowerCase()).append(" ");
            }
        }

        return newContestTitle.toString().trim();
    }

    public String getFormatContestTitleInQuotes(){
        return "«" + getFormatContestTitle() + "»";
    }

    public String getSportTitleLowerCase(){
        return sportTitle.toLowerCase();
    }

    public String getSportTitleLowerCaseInQuotes(){
        return "«" + getSportTitleLowerCase() + "»";
    }

    public String getFormatAgeGroups(){
        Set<String> groupSet = new HashSet<>();
        for(SportDTO discipline : disciplines){
            String formatData = discipline.getGroup().getTitle().toLowerCase();
            groupSet.add(formatData);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String group : groupSet){
            stringBuilder.append(group).append(", ");
        }
        return stringBuilder.substring(0,stringBuilder.toString().length() - 2);
    }

    public String getFormatDisciplines(){
        Set<String> disciplineSet = new HashSet<>();
        for(SportDTO discipline : disciplines){
            String formatData = discipline.getDiscipline().getTitle().toLowerCase();
            disciplineSet.add(formatData);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String discipline : disciplineSet){
            stringBuilder.append(discipline).append(", ");
        }
        return stringBuilder.substring(0,stringBuilder.toString().length() - 2);
    }

    public String getFormatDisciplinesInQuotes(){
        Set<String> disciplineSet = new HashSet<>();
        for(SportDTO discipline : disciplines){
            String formatData = discipline.getDiscipline().getTitle().toLowerCase();
            disciplineSet.add("«" + formatData + "»");
        }
        StringBuilder stringBuilder = new StringBuilder();
        for(String discipline : disciplineSet){
            stringBuilder.append(discipline).append(", ");
        }
        return stringBuilder.substring(0,stringBuilder.toString().length() - 2);
    }

    public String getBeginningDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(beginning);
    }

    public String getEndingDate(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(ending);
    }

    public String getContestDate(){
        return getBeginningDate() + " - " + getEndingDate();
    }

    public String getFullLocation(){
        Set<String> stringSet = new HashSet<>(Arrays.asList(subjectTitle,city));
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : stringSet){
            stringBuilder.append(s).append(", ");
        }
        return stringBuilder.substring(0,stringBuilder.toString().length() - 2);
    }

    public String getFormatContestQualificationAllow(){
        StringBuilder builder = new StringBuilder();
        String[] titles = {"третий юношеский разряд", "второй юношеский разряд", "первый юношеский разряд",
                "третий разряд", "второй разряд", "первый разряд",
                "кандидаты в мастера спорта", "мастера спорта России", "мастера спорта международного класса",
                "заслуженные мастера спорта"};
        int[] dates = {yn3Date, yn2Date, yn1Date, r3Date, r2Date, r1Date, kmsDate, msDate, msmkDate, zmsDate};

        for (int i = 0; i < dates.length; i++) {
            int date = dates[i];
            if (date != 0) {
                builder.append(titles[i]);
                if (date == 1 || date == 21 || date == 31 || date == 41) {
                    builder.append(" с ").append(date).append(" года, ");
                } else {
                    builder.append(" с ").append(date).append(" лет, ");
                }
            }
        }
        return builder.substring(0,builder.toString().length() - 2);
    }

    public String getFederalStandard(){
        List<FederalStandard> fsl = new ArrayList<>();
        for(SportDTO discipline : disciplines){
            for(FederalStandard fs : discipline.getStandards()){
                if(fs != null){
                    fsl.add(fs);
                }
            }
        }
        fsl = fsl.stream().sorted(Comparator.comparing(FederalStandard::getCount)).distinct().collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for(FederalStandard fs : fsl){
            builder.append(fs.getTitle().toLowerCase()).append(", ");
        }
        return builder.substring(0,builder.toString().length() - 2);
    }
}
