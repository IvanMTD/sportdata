package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class ContestMonitoringDTO {
    private String contestTitle;
    private String sportTitle;

    private List<SportDTO> disciplines = new ArrayList<>();

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
            String formatData = discipline.getGroup().getTitle().toLowerCase() + " до " + discipline.getGroup().getMaxAge() + " лет";
            groupSet.add(formatData);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for(String group : groupSet){
            stringBuilder.append(group).append(", ");
        }
        return stringBuilder.toString().substring(0,stringBuilder.toString().length() - 2);
    }
}
