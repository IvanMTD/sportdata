package ru.fcpsr.sportdata.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.PasswordDTO;
import ru.fcpsr.sportdata.dto.UserDTO;
import ru.fcpsr.sportdata.models.Role;
import ru.fcpsr.sportdata.models.SysUser;
import ru.fcpsr.sportdata.services.UserService;
import ru.fcpsr.sportdata.validators.PasswordValidation;
import ru.fcpsr.sportdata.validators.UserValidator;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final PasswordValidation passwordValidation;

    /************************
     * БЛОК АУТЕНТИФИКАЦИИ  *
     * @return              *
     ************************/

    @GetMapping("/login")
    public Mono<Rendering> loginPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Login page")
                        .modelAttribute("index","login-page")
                        .build()
        );
    }

    /********************************
     * БЛОК РАБОТЫ С ПОЛЬЗОВАТЕЛЯМИ *
     * @return                      *
     ********************************/

    @GetMapping("/user/reg")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Rendering> userReg(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Registration")
                        .modelAttribute("index","user-reg-page")
                        .modelAttribute("user", new UserDTO())
                        .modelAttribute("roles", Role.values())
                        .build()
        );
    }

    @PostMapping("/user/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Rendering> userAdd(@ModelAttribute(name = "user") @Valid UserDTO user, Errors errors){
        return userService.getAll().collectList().flatMap(users -> {
            userValidator.validate(user,errors);
            userValidator.validate(user,errors,users);
            if(errors.hasErrors()){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Registration")
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
        });
    }

    @GetMapping("/user/profile")
    public Mono<Rendering> profilePage(@AuthenticationPrincipal SysUser user){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Profile page")
                        .modelAttribute("index","profile-page")
                        .modelAttribute("user", userService.findByUsername(user.getUsername()).flatMap(u -> Mono.just(new UserDTO((SysUser)u))))
                        .modelAttribute("password",new PasswordDTO())
                        .modelAttribute("roles", Role.values())
                        .modelAttribute("edit",false)
                        .modelAttribute("pass",false)
                        .build()
        );
    }

    @PostMapping("/user/update")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public Mono<Rendering> update(ServerWebExchange exchange, @ModelAttribute(name = "user") @Valid UserDTO user, Errors errors){
        return userService.getAll().collectList().flatMap(users -> {
            userValidator.validateWithOutUser(user,errors,users);
            if(errors.hasFieldErrors("username") || errors.hasFieldErrors("email")){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Profile page")
                                .modelAttribute("index","profile-page")
                                .modelAttribute("user", user)
                                .modelAttribute("roles", Role.values())
                                .modelAttribute("password",new PasswordDTO())
                                .modelAttribute("edit",true)
                                .modelAttribute("pass",false)
                                .build()
                );
            }

            return userService.updateUserData(user).flatMap(u -> {
                log.info("user saved: " + u.toString());
                return Mono.just(Rendering.redirectTo("/auth/user/profile").build());
            });
        });
    }

    @PostMapping("/user/update/password")
    @PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
    public Mono<Rendering> updateUserPassword(@AuthenticationPrincipal SysUser user, @ModelAttribute(name = "password") @Valid PasswordDTO passwordDTO, Errors errors){
        return userService.getById(user.getId()).flatMap(u -> {
            passwordValidation.validate(passwordDTO,errors);
            passwordValidation.checkOldPassword(passwordDTO,errors,u);
            if(errors.hasErrors()){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Profile page")
                                .modelAttribute("index","profile-page")
                                .modelAttribute("user", userService.findByUsername(user.getUsername()).flatMap(u2 -> Mono.just(new UserDTO((SysUser)u2))))
                                .modelAttribute("roles", Role.values())
                                .modelAttribute("password",passwordDTO)
                                .modelAttribute("edit",false)
                                .modelAttribute("pass",true)
                                .build()
                );
            }

            return userService.updateUserPassword(user.getId(), passwordDTO).flatMap(u3 -> {
                log.info("user password updated: " + u3.toString());
                return Mono.just(Rendering.redirectTo("/auth/user/profile").build());
            });
        });
    }
}
