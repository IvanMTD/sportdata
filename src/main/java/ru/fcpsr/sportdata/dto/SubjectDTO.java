package ru.fcpsr.sportdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.fcpsr.sportdata.enums.FederalDistrict;
import ru.fcpsr.sportdata.models.Subject;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectDTO {
    private int id;
    @NotBlank(message = "Поле не может быть пустым!")
    private String title;
    @NotBlank(message = "Поле не может быть пустым!")
    @Pattern(regexp = "\\D\\D-\\w+",message = "Введите значение в соответствии с ISO 3166")
    private String iso;
    private FederalDistrict federalDistrict;
    private List<SportSchoolDTO> schools = new ArrayList<>();
    private List<BaseSportDTO> baseSports = new ArrayList<>();

    public SubjectDTO(Subject subject){
        setId(subject.getId());
        setTitle(subject.getTitle());
        setIso(subject.getIso());
        if(subject.getFederalDistrict() != null){
            setFederalDistrict(subject.getFederalDistrict());
        }else{
            setFederalDistrict(FederalDistrict.NO);
        }
    }
}
