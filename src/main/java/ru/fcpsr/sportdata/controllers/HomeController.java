package ru.fcpsr.sportdata.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.services.BaseSportService;
import ru.fcpsr.sportdata.services.SubjectService;
import ru.fcpsr.sportdata.services.TypeOfSportService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final SubjectService subjectService;
    private final BaseSportService baseSportService;
    private final TypeOfSportService sportService;

    @GetMapping("/")
    public Mono<Rendering> mainPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Main page")
                        .modelAttribute("index","main-page")
                        .modelAttribute("subjects", getCompletedSubjects())
                        .build()
        );
    }

    private Flux<SubjectDTO> getCompletedSubjects(){
        return subjectService.getAll().flatMap(subject -> {
            SubjectDTO subjectDTO = new SubjectDTO(subject);
            return baseSportService.getAllByIds(subject.getBaseSportIds()).flatMap(baseSport -> {
                BaseSportDTO baseSportDTO = new BaseSportDTO(baseSport);
                return sportService.getById(baseSport.getTypeOfSportId()).flatMap(sport -> {
                    TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                    baseSportDTO.setSport(typeOfSportDTO);
                    return Mono.just(baseSportDTO);
                });
            }).collectList().flatMap(baseSportDTOS -> {
                subjectDTO.setBaseSports(baseSportDTOS);
                return Mono.just(subjectDTO);
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
}
