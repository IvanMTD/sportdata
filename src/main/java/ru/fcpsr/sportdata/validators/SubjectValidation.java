package ru.fcpsr.sportdata.validators;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.fcpsr.sportdata.dto.SubjectDTO;
import ru.fcpsr.sportdata.models.Subject;

@Data
@Component
public class SubjectValidation implements Validator {

    private Subject subject;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SubjectDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SubjectDTO subjectDTO = (SubjectDTO) target;
        if(subject.getTitle() != null) {
            if (subject.getTitle().equals(subjectDTO.getTitle())) {
                errors.rejectValue("title", "", "Регион с таким именем уже существует!");
            }
        }
        if(subject.getIso() != null){
            if(subject.getIso().equals(subjectDTO.getIso())){
                errors.rejectValue("iso", "", "Регион с таким ISO уже существует!");
            }
        }
    }
}
