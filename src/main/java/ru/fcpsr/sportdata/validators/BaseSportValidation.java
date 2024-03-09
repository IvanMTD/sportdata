package ru.fcpsr.sportdata.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.fcpsr.sportdata.dto.BaseSportDTO;

import java.time.LocalDate;

@Component
public class BaseSportValidation implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(BaseSportDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        BaseSportDTO baseSport = (BaseSportDTO) target;
        if(baseSport.getExpiration() < LocalDate.now().getYear()){
            errors.rejectValue("expiration","","Год окончания не может быть ниже текущего!");
        }
    }
}
