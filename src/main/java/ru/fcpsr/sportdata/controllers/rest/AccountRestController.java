package ru.fcpsr.sportdata.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.MyResponse;
import ru.fcpsr.sportdata.dto.UserDTO;
import ru.fcpsr.sportdata.models.SysUser;
import ru.fcpsr.sportdata.services.UserService;

@Slf4j
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountRestController {

    private final UserService userService;

    @GetMapping("/password/check")
    public Mono<Boolean> checkPassword(@AuthenticationPrincipal SysUser user, @RequestParam(name = "password") String password){
        return userService.getById(user.getId()).flatMap(u -> Mono.just(userService.getEncoder().matches(password,u.getPassword())));
    }

    @GetMapping("/user/password/update")
    public Mono<ResponseEntity<MyResponse>> changePassword(@AuthenticationPrincipal SysUser user, @RequestParam(name = "password") String password){
        return userService.getById(user.getId()).flatMap(u -> {
            u.setPassword(userService.getEncoder().encode(password));
            return userService.save(u).flatMap(saved -> Mono.just(ResponseEntity.ok().body(new MyResponse(saved, "Смена пароля прошла успешно"))));
        }).onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MyResponse(null,"Не удалось сменить пароль"))));
    }
}
