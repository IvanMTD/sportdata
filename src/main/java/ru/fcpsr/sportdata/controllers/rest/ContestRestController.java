package ru.fcpsr.sportdata.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ContestDTO;
import ru.fcpsr.sportdata.dto.RestContest;
import ru.fcpsr.sportdata.dto.TypeOfSportDTO;
import ru.fcpsr.sportdata.enums.SportFilterType;
import ru.fcpsr.sportdata.services.ContestService;
import ru.fcpsr.sportdata.services.TypeOfSportService;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contest")
public class ContestRestController {

    private final ContestService contestService;
    private final TypeOfSportService sportService;

    @GetMapping("/get/all")
    public Mono<RestContest> getAllContests(@RequestParam(name = "year") int year){
        return contestService.getAllByYear(year).flatMap(contest -> {
            ContestDTO contestDTO = new ContestDTO(contest);
            return sportService.findById(contest.getTypeOfSportId()).flatMap(sport -> {
                TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                contestDTO.setSport(typeOfSportDTO);
                return Mono.just(contestDTO);
            });
        }).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(ContestDTO::getSportTitle)).collect(Collectors.toList());
            RestContest restContest = new RestContest();
            restContest.setTotal(l.size());
            for(ContestDTO contest : l){
                if(contest.getSport().getSportFilterType().equals(SportFilterType.OLYMPIC)){
                    restContest.getOlympic().add(contest);
                }else if(contest.getSport().getSportFilterType().equals(SportFilterType.NO_OLYMPIC)){
                    restContest.getNoOlympic().add(contest);
                }else if(contest.getSport().getSportFilterType().equals(SportFilterType.ADAPTIVE)){
                    restContest.getAdaptive().add(contest);
                }
            }
            return Mono.just(restContest);
        });
    }
}
