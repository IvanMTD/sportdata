package ru.fcpsr.sportdata.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.FederalStandard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SportDTO {
    private int id;

    private int disciplineId;
    private DisciplineDTO discipline;
    private int groupId;
    private AgeGroupDTO group;
    private List<Category> allowed = new ArrayList<>(Collections.nCopies(Category.values().length, null));
    private List<CategoryDTO> allowedList = new ArrayList<>();
    private List<FederalStandard> standards = new ArrayList<>(Collections.nCopies(FederalStandard.values().length, null));
    private List<FederalStandardDTO> standardList = new ArrayList<>();
    List<PlaceDTO> places = new ArrayList<>();

    public SportDTO(ArchiveSport sport){
        setId(sport.getId());

        setDisciplineId(sport.getDisciplineId());
        setGroupId(sport.getAgeGroupId());

        if(sport.getAllowed() != null) {
            allowed.addAll(sport.getAllowed());
            setAllowedList(allowed);
        }
        if(sport.getFederalStandards() != null) {
            standards.addAll(sport.getFederalStandards());
            setStandardList(standards);
        }
    }

    public void setAllowedList(List<Category> allowed) {
        for(Category category : allowed){
            if(category != null){
                CategoryDTO categoryDTO = new CategoryDTO(category);
                allowedList.add(categoryDTO);
            }
        }
        allowedList = allowedList.stream().sorted(Comparator.comparing(CategoryDTO::getTitle)).collect(Collectors.toList());
    }

    public void setStandardList(List<FederalStandard> fs) {
        for(FederalStandard federalStandard : fs){
            if(federalStandard != null){
                FederalStandardDTO federalStandardDTO = new FederalStandardDTO(federalStandard);
                standardList.add(federalStandardDTO);
            }
        }
        standardList = standardList.stream().sorted(Comparator.comparing(FederalStandardDTO::getTitle)).collect(Collectors.toList());
    }

    public String getAllowedString(){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<10; i++){
            if(i==0){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.YN3)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==1){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.YN2)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==2){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.YN1)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==3){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.R3)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==4){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.R2)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==5){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.R1)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==6){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.KMS)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==7){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.MS)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else if(i==8){
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.MSMK)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }else {
                for(CategoryDTO categoryDTO : allowedList){
                    if(categoryDTO.getCategory().equals(Category.ZMS)){
                        builder.append(categoryDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }
        }

        return getFinalCut(builder.toString());
    }

    public String getStandardString(){
        StringBuilder builder = new StringBuilder();

        for(int i=0; i<4; i++){
            if(i==0){
                for(FederalStandardDTO federalStandardDTO : standardList){
                    if(federalStandardDTO.getFederalStandard().equals(FederalStandard.NP)){
                        builder.append(federalStandardDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }
            if(i==1){
                for(FederalStandardDTO federalStandardDTO : standardList){
                    if(federalStandardDTO.getFederalStandard().equals(FederalStandard.UTE)){
                        builder.append(federalStandardDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }
            if(i==2){
                for(FederalStandardDTO federalStandardDTO : standardList){
                    if(federalStandardDTO.getFederalStandard().equals(FederalStandard.SSM)){
                        builder.append(federalStandardDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }
            if(i==3){
                for(FederalStandardDTO federalStandardDTO : standardList){
                    if(federalStandardDTO.getFederalStandard().equals(FederalStandard.VSM)){
                        builder.append(federalStandardDTO.getTitle().toLowerCase()).append("|");
                    }
                }
            }
        }

        return getFinalCut(builder.toString());
    }

    private String getFinalCut(String data){
        String[] part = data.split("\\|");
        StringBuilder finalCut = new StringBuilder();
        for(int i=0; i<part.length; i++){
            if(i == part.length - 1){
                finalCut.append(part[i]);
            }else{
                finalCut.append(part[i]).append(", ");
            }
        }

        return finalCut.toString();
    }
}
