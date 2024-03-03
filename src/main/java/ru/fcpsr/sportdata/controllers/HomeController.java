package ru.fcpsr.sportdata.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {
    @GetMapping("/")
    public Mono<Rendering> mainPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Main page")
                        .modelAttribute("index","main-page")
                        .build()
        );
    }
}
