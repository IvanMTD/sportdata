package ru.fcpsr.sportdata.validators;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.models.Participant;

@Data
@Component
public class ParticipantValidation implements Validator {

    private Participant participant;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(ParticipantDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(participant != null && participant.getName() != null && participant.getBirthday() != null){
            ParticipantDTO participantDTO = (ParticipantDTO) target;
            if(participant.getFullName().equals(participantDTO.getFullName())){
                if(participant.getDate().equals(participantDTO.getDate())){
                    errors.rejectValue("birthday","","Такой спортсмен уже существует! Вы не можете добавить уже существующего спортсмена!");
                }
            }
        }
    }
}
