package ru.fcpsr.sportdata.configurations.handlers;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.util.NamingUtil;

public class JwtLogoutSuccessHandler extends RedirectServerLogoutSuccessHandler {
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        response.addCookie(ResponseCookie.from(NamingUtil.getInstance().getAccessName(), "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build());
        response.addCookie(ResponseCookie.from(NamingUtil.getInstance().getRefreshName(), "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build());

        return super.onLogoutSuccess(webFilterExchange, authentication);
    }
}
