package ru.fcpsr.sportdata.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Role;
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

    @ModelAttribute(name = "admin")
    public Mono<Boolean> iaAdmin(@AuthenticationPrincipal SysUser user){
        if(user != null){
            if(user.getRole().equals(Role.ADMIN)){
                return Mono.just(true);
            }else{
                return Mono.just(false);
            }
        }else{
            return Mono.just(false);
        }
    }

    @ModelAttribute(name = "manager")
    public Mono<Boolean> isManager(@AuthenticationPrincipal SysUser user){
        if(user != null){
            if(user.getRole().equals(Role.MANAGER)){
                return Mono.just(true);
            }else{
                return Mono.just(false);
            }
        }else{
            return Mono.just(false);
        }
    }
}
