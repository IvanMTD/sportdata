package ru.fcpsr.sportdata.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.SysUser;

@ControllerAdvice
public class SecurityControllerAdvice {
    @ModelAttribute(name = "auth")
    public Mono<Boolean> isAuthenticate(@AuthenticationPrincipal SysUser user){
        if(user != null){
            return Mono.just(true);
        }else{
            return Mono.just(false);
        }
    }
}
