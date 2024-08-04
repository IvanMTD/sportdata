package ru.fcpsr.sportdata.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.enums.SportFilterType;
import ru.fcpsr.sportdata.services.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contest")
public class ContestRestController {

    private final ContestService contestService;
    private final ArchiveSportService archiveSportService;
    private final PlaceService placeService;
    private final TypeOfSportService sportService;
    private final ParticipantService participantService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;

    @GetMapping("/get/all")
    public Mono<RestContest> getAllContests(@RequestParam(name = "year") int year){
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return getCompleteDate(startOfYear,endOfYear);
    }

    @GetMapping("/get/all/by/date")
    public Mono<RestContest> getAllByDate(@RequestParam(name = "start") LocalDate start, @RequestParam(name = "end") LocalDate end){
        return getCompleteDate(start,end);
    }

    private Mono<RestContest> getCompleteDate(LocalDate start, LocalDate end){
        return contestService.getAllByDate(start,end).flatMap(contest -> {
            ContestDTO contestDTO = new ContestDTO(contest);
            return sportService.findById(contest.getTypeOfSportId()).flatMap(sport -> {
                TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                contestDTO.setSport(typeOfSportDTO);
                return archiveSportService.getAllByIdIn(contest.getASportIds()).flatMap(archiveSport -> {
                    SportDTO sportDTO = new SportDTO(archiveSport);
                    return disciplineService.getById(archiveSport.getDisciplineId()).flatMap(discipline -> {
                        DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
                        sportDTO.setDiscipline(disciplineDTO);
                        return groupService.getById(archiveSport.getAgeGroupId()).flatMap(ageGroup -> {
                            AgeGroupDTO ageGroupDTO = new AgeGroupDTO(ageGroup);
                            sportDTO.setGroup(ageGroupDTO);
                            return placeService.getAllByIdIn(archiveSport.getPlaceIds()).flatMap(place -> {
                                PlaceDTO placeDTO = new PlaceDTO(place);
                                return participantService.getById(place.getParticipantId()).flatMap(participant -> {
                                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                                    placeDTO.setParticipant(participantDTO);
                                    return Mono.justOrEmpty(placeDTO);
                                });
                            }).collectList().flatMap(placeDTOS -> {
                                placeDTOS = placeDTOS.stream().sorted(Comparator.comparing(PlaceDTO::getPlace)).collect(Collectors.toList());
                                sportDTO.setPlaces(placeDTOS);
                                return Mono.just(sportDTO);
                            });
                        });
                    });
                }).collectList().flatMap(archiveSportDTOS -> {
                    archiveSportDTOS = archiveSportDTOS.stream().sorted(Comparator.comparing(SportDTO::getId)).collect(Collectors.toList());
                    contestDTO.setSports(archiveSportDTOS);
                    return Mono.just(contestDTO);
                });
            });
        }).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(ContestDTO::getSportTitle)).collect(Collectors.toList());
            RestContest restContest = new RestContest();
            restContest.setTotal(l.size());
            restContest.setContests(l);
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
