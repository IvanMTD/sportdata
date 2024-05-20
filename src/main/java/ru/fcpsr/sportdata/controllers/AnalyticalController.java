package ru.fcpsr.sportdata.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/analytics")
public class AnalyticalController {
    @GetMapping("/contest")
    public Mono<Rendering> getContestAnalytics(@RequestParam(name = "contest") long id){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Contest analytics")
                        .modelAttribute("index","analytics-contest-page")
                        .build()
        );
    }
}
