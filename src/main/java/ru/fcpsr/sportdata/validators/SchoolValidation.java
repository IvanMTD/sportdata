package ru.fcpsr.sportdata.validators;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.fcpsr.sportdata.dto.SportSchoolDTO;
import ru.fcpsr.sportdata.models.SportSchool;

@Slf4j
@Data
@Component
public class SchoolValidation implements Validator {

    private SportSchool sportSchool;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SportSchoolDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SportSchoolDTO sportSchoolDTO = (SportSchoolDTO) target;
        if(sportSchoolDTO.getInn().equals(sportSchool.getInn())){
            if(sportSchoolDTO.getId() != 0){
                if(sportSchoolDTO.getId() != sportSchool.getId()){
                    errors.rejectValue("title", "", "Вы пытаетесь добавить уже существующую в базе данных организацию!");
                }
            }else {
                errors.rejectValue("title", "", "Вы пытаетесь добавить уже существующую в базе данных организацию!");
            }
        }
    }
}
