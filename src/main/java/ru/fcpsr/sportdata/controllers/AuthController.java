package ru.fcpsr.sportdata.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.UserDTO;
import ru.fcpsr.sportdata.models.Role;
import ru.fcpsr.sportdata.services.UserService;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /************************
     * БЛОК АУТЕНТИФИКАЦИИ  *
     * @return              *
     ************************/

    @GetMapping("/login")
    public Mono<Rendering> loginPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","login")
                        .modelAttribute("index","login-page")
                        .build()
        );
    }

    /********************************
     * БЛОК РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ *
     * @return                      *
     ********************************/

    @GetMapping("/user/reg")
    public Mono<Rendering> userReg(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","registration")
                        .modelAttribute("index","user-reg-page")
                        .modelAttribute("user", new UserDTO())
                        .modelAttribute("roles", Role.values())
                        .build()
        );
    }

    @PostMapping("/user/add")
    public Mono<Rendering> userAdd(@ModelAttribute(name = "user") @Valid UserDTO user, Errors errors){
        if(errors.hasErrors()){
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","registration")
                            .modelAttribute("index","user-reg-page")
                            .modelAttribute("user", user)
                            .modelAttribute("roles", Role.values())
                            .build()
            );
        }
        return userService.saveUser(user).flatMap(u -> {
            log.info("user has been saved - " + u.toString());
            return Mono.just(Rendering.redirectTo("/").build());
        });
    }
}
