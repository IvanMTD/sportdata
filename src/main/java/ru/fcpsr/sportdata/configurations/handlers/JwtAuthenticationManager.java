package ru.fcpsr.sportdata.configurations.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.component.JWT;
import ru.fcpsr.sportdata.services.UserService;
import ru.fcpsr.sportdata.util.NamingUtil;

import java.time.Duration;

@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JWT jwt;
    private final UserService userService;
    private final PasswordEncoder encoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String accessToken = authentication.getPrincipal().toString();
        String refreshToken = authentication.getCredentials().toString();

        if(jwt.validateToken(accessToken)){
            return userService.findByUsername(jwt.getUsernameFromToken(accessToken)).flatMap(user -> Mono.just(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities())));
        }else if(jwt.validateToken(refreshToken)){
            return Mono.deferContextual(contextView -> {
                ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
                return userService.findByUsername(jwt.getUsernameFromToken(refreshToken)).flatMap(user -> {
                    String username = user.getUsername();
                    String digitalSignature = exchange.getRequest().getHeaders().getFirst("User-Agent");
                    String newAccessToken = jwt.generateAccessToken(username, digitalSignature);
                    String newRefreshToken = jwt.generateRefreshToken(username, digitalSignature);
                    ResponseCookie accessCookie = ResponseCookie.from(NamingUtil.getInstance().getAccessName(), newAccessToken)
                            .httpOnly(true)
                            .maxAge(Duration.ofHours(1))
                            .path("/")
                            .build();
                    ResponseCookie refreshCookie = ResponseCookie.from(NamingUtil.getInstance().getRefreshName(), newRefreshToken)
                            .httpOnly(true)
                            .maxAge(Duration.ofDays(30))
                            .path("/")
                            .build();

                    exchange.getResponse().addCookie(accessCookie);
                    exchange.getResponse().addCookie(refreshCookie);
                    return Mono.just(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
                }).switchIfEmpty(Mono.error(new BadCredentialsException("Authentication failed")));
            });
        }else{
            String username = authentication.getPrincipal().toString();
            String password = authentication.getCredentials().toString();
            return userService.findByUsername(username).flatMap(user -> {
                if(encoder.matches(password,user.getPassword())){
                    return Mono.just(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
                } else{
                    return Mono.error(new BadCredentialsException("Authentication failed"));
                }
            }).cast(Authentication.class).switchIfEmpty(Mono.error(new BadCredentialsException("Authentication failed")));
        }
    }
}
