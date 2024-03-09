package ru.fcpsr.sportdata.validators;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.fcpsr.sportdata.dto.UserDTO;
import ru.fcpsr.sportdata.models.SysUser;
import ru.fcpsr.sportdata.services.UserService;

import java.util.List;

@Slf4j
@Component
public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO user = (UserDTO) target;
        if(!user.getPassword().equals(user.getConfirm())){
            errors.rejectValue("confirm","","Пароль и подтверждение пароля не совпадают");
        }
    }

    public void validate(Object target, Errors errors, List<SysUser> users) {
        UserDTO user = (UserDTO) target;
        for(SysUser u : users){
            if (u.getEmail().equals(user.getEmail())) {
                errors.rejectValue("email", "", "Такой email уже зарегистрирован");
            }
            if (u.getUsername().equals(user.getUsername())) {
                errors.rejectValue("username", "", "Такое имя пользователя уже зарегистрировано");
            }
        }
    }

    public void validateWithOutUser(Object target, Errors errors, List<SysUser> users) {
        UserDTO user = (UserDTO) target;
        for(SysUser u : users){
            if(user.getId() != u.getId()) {
                if (u.getEmail().equals(user.getEmail())) {
                    errors.rejectValue("email", "", "Такой email уже зарегистрирован");
                }
                if (u.getUsername().equals(user.getUsername())) {
                    errors.rejectValue("username", "", "Такое имя пользователя уже зарегистрировано");
                }
            }
        }
    }
}
