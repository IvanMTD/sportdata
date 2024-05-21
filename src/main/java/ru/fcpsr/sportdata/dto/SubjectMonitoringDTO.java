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
        if(
            subjectsForSportIsBasic.size() == 1 ||
            subjectsForSportIsBasic.size() == 21 ||
            subjectsForSportIsBasic.size() == 31 ||
            subjectsForSportIsBasic.size() == 41 ||
            subjectsForSportIsBasic.size() == 51 ||
            subjectsForSportIsBasic.size() == 61 ||
            subjectsForSportIsBasic.size() == 71 ||
            subjectsForSportIsBasic.size() == 81 ||
            subjectsForSportIsBasic.size() == 91
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
}
