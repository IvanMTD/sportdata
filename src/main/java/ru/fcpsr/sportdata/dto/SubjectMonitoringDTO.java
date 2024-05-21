package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class SubjectMonitoringDTO {
    private List<SubjectDTO> subjectFirstPlace = new ArrayList<>();
    private List<SubjectDTO> subjectSecondPlace = new ArrayList<>();
    private List<SubjectDTO> subjectLastPlace = new ArrayList<>();

    private List<SubjectDTO> subjectsTookPart = new ArrayList<>();
    private List<SubjectDTO> subjectsForSportIsBasic = new ArrayList<>();

    private List<SubjectDTO> subjectsTookPartAndSportIsBasic = new ArrayList<>();
    private List<SubjectDTO> subjectsTookPartAndSportIsNotBasic = new ArrayList<>();
    private List<SubjectDTO> subjectsDidNotPartAndSportIsBasic = new ArrayList<>();

    public void setupSubjects(){
        calcSubjectsTookPartAndSportIsBasic();
        calcSubjectTookPartAndSportIsNotBasic();
        calcSubjectDidNonPartAndSportIsBasic();
        log.info("took part size is [{}]", subjectsTookPart.size());
        log.info("base sport total size is [{}]", subjectsForSportIsBasic.size());
    }

    private void calcSubjectsTookPartAndSportIsBasic() {
        log.info("the preparation of the subject who took part, and sport is basic for him");
        subjectsTookPartAndSportIsBasic = subjectsTookPart
                .stream()
                .filter(subjectTookPart -> subjectsForSportIsBasic.contains(subjectTookPart))
                .collect(Collectors.toList());
        subjectsTookPartAndSportIsBasic = subjectsTookPartAndSportIsBasic.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
        log.info("setup complete size is [{}]", subjectsTookPartAndSportIsBasic.size());
    }

    private void calcSubjectTookPartAndSportIsNotBasic(){
        log.info("the preparation of the subject who took part, and sport is not basic for him");
        subjectsTookPartAndSportIsNotBasic = subjectsTookPart
                .stream()
                .filter(subject -> subjectsTookPartAndSportIsBasic.stream().noneMatch(s -> s.getId() == subject.getId()))
                .collect(Collectors.toList());
        subjectsTookPartAndSportIsNotBasic = subjectsTookPartAndSportIsNotBasic.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
        log.info("setup complete size is [{}]", subjectsTookPartAndSportIsNotBasic.size());
    }

    private void calcSubjectDidNonPartAndSportIsBasic(){
        log.info("the preparation of the subject who did not part, and sport is basic for him");
        List<SubjectDTO> temp = new ArrayList<>(subjectsForSportIsBasic);
        temp.removeAll(subjectsTookPartAndSportIsBasic);
        subjectsDidNotPartAndSportIsBasic = new ArrayList<>(temp);
        subjectsDidNotPartAndSportIsBasic = subjectsDidNotPartAndSportIsBasic.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
        log.info("setup complete size is [{}]", subjectsDidNotPartAndSportIsBasic.size());
    }

    public String getEndingForBaseSportSubjects(){
        return getFormat(subjectsForSportIsBasic);
    }

    public String getEndingForSubjectsTookPart(){
        return getFormat(subjectsTookPart);
    }

    public String getEndingForSubjectDidNonTookPartAndSportIsBasic(){
        return getFormat(subjectsDidNotPartAndSportIsBasic);
    }

    private String getFormat(List<SubjectDTO> subjectList) {
        if(
            subjectList.size() == 1 ||
            subjectList.size() == 21 ||
            subjectList.size() == 31 ||
            subjectList.size() == 41 ||
            subjectList.size() == 51 ||
            subjectList.size() == 61 ||
            subjectList.size() == 71 ||
            subjectList.size() == 81 ||
            subjectList.size() == 91
        ){
            return "а";
        }else{
            return "ов";
        }
    }

    public String getFormatSubjectsTookPart(){
        List<String> sbs = new ArrayList<>();
        for(SubjectDTO subjectTookPartAndSportIsBasic : subjectsTookPartAndSportIsBasic){
            sbs.add(subjectTookPartAndSportIsBasic.getTitle() + "*");
        }
        List<String> snbs = new ArrayList<>();
        for(SubjectDTO subjectTookPartAndSportIsNotBasic : subjectsTookPartAndSportIsNotBasic){
            snbs.add(subjectTookPartAndSportIsNotBasic.getTitle());
        }
        List<String> result = new ArrayList<>(sbs);
        result.addAll(snbs);
        result = result.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        for(String subjectTitle : result){
            stringBuilder.append(subjectTitle).append(", ");
        }
        return stringBuilder.substring(0,stringBuilder.toString().length() - 2);
    }

    public String getFormatSubjectDidNotTookPartAndSportIsBasic(){
        if(subjectsDidNotPartAndSportIsBasic.size() == 0){
            return "";
        }else {
            List<String> subjectTitles = new ArrayList<>();
            for (SubjectDTO subjectDidNotTookPartAndSportIsBasic : subjectsDidNotPartAndSportIsBasic) {
                subjectTitles.add(subjectDidNotTookPartAndSportIsBasic.getTitle());
            }
            StringBuilder stringBuilder = new StringBuilder();
            for (String subjectTitle : subjectTitles) {
                stringBuilder.append(subjectTitle).append(", ");
            }
            return stringBuilder.substring(0, stringBuilder.toString().length() - 2);
        }
    }
}
