package ru.fcpsr.sportdata.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.services.ParticipantService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/database/")
public class DataRestController {

    private final ParticipantService participantService;

    @GetMapping("/participants")
    public Flux<Participant> getParticipants(@RequestParam(name = "query") String query){
        return participantService.findAllByLastnameLike(query);
    }
}
