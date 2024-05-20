package ru.fcpsr.sportdata.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.AgeGroupDTO;
import ru.fcpsr.sportdata.dto.ContestMonitoringDTO;
import ru.fcpsr.sportdata.dto.DisciplineDTO;
import ru.fcpsr.sportdata.dto.SportDTO;
import ru.fcpsr.sportdata.services.AgeGroupService;
import ru.fcpsr.sportdata.services.ArchiveSportService;
import ru.fcpsr.sportdata.services.ContestService;
import ru.fcpsr.sportdata.services.DisciplineService;

import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticalController {

    private final ContestService contestService;
    private final ArchiveSportService archiveSportService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;

    @GetMapping("/contest")
    public Mono<Rendering> getContestAnalytics(@RequestParam(name = "contest") int id){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Contest analytics")
                        .modelAttribute("index","analytics-contest-page")
                        .modelAttribute("monitoring", getContestMonitoring(id))
                        .build()
        );
    }

    private Mono<ContestMonitoringDTO> getContestMonitoring(int id){
        return contestService.getById(id).flatMap(contest -> {
            ContestMonitoringDTO monitoringDTO = new ContestMonitoringDTO();
            monitoringDTO.setContestTitle(contest.getTitle());
            monitoringDTO.setSportTitle(contest.getSportTitle());
            return archiveSportService.getAllByIdIn(contest.getASportIds()).flatMap(archiveSport -> {
                SportDTO sportDTO = new SportDTO(archiveSport);
                return disciplineService.getById(archiveSport.getDisciplineId()).flatMap(discipline -> {
                    sportDTO.setDiscipline(new DisciplineDTO(discipline));
                    return groupService.getById(archiveSport.getAgeGroupId()).flatMap(group -> {
                        sportDTO.setGroup(new AgeGroupDTO(group));
                        return Mono.just(sportDTO);
                    });
                });
            }).collectList().flatMap(l -> {
                l = l.stream().sorted(Comparator.comparing(SportDTO::getId)).collect(Collectors.toList());
                monitoringDTO.setDisciplines(l);
                return Mono.just(monitoringDTO);
            });
        });
    }
}
