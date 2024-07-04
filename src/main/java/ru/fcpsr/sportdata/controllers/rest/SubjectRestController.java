package ru.fcpsr.sportdata.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.services.SubjectService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subject")
public class SubjectRestController {
    private final SubjectService subjectService;

    @GetMapping("/get/all")
    public Flux<Subject> getAllSubjects(){
        return subjectService.getAll().flatMap(subject -> {
            log.info("found subject [{}]",subject);
            return Mono.just(subject);
        });
    }

}
