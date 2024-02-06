package ru.fcpsr.sportdata.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.AgeGroupDTO;
import ru.fcpsr.sportdata.dto.DisciplineDTO;
import ru.fcpsr.sportdata.dto.TypeOfSportDTO;
import ru.fcpsr.sportdata.models.AgeGroup;
import ru.fcpsr.sportdata.models.Discipline;
import ru.fcpsr.sportdata.services.AgeGroupService;
import ru.fcpsr.sportdata.services.DisciplineService;
import ru.fcpsr.sportdata.services.TypeOfSportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sport")
public class SportController {

    private final TypeOfSportService sportService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;

    @GetMapping()
    public Mono<Rendering> sportPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","sport")
                        .modelAttribute("index","sport-page")
                        .modelAttribute("sports",
                                sportService.getAll().flatMap(sport -> {
                                    TypeOfSportDTO sportDTO = new TypeOfSportDTO(sport);
                                    return disciplineService.getAllByIds(sport.getDisciplineIds()).collectList().flatMap(disciplines -> {
                                        List<DisciplineDTO> disciplineDTOList = new ArrayList<>();
                                        List<Integer> groupIds = new ArrayList<>();
                                        for(Discipline discipline : disciplines){
                                            DisciplineDTO disciplineDTO = new DisciplineDTO();
                                            disciplineDTO.setId(discipline.getId());
                                            disciplineDTO.setTitle(discipline.getTitle());
                                            disciplineDTOList.add(disciplineDTO);
                                            groupIds.addAll(discipline.getAgeGroupIds());
                                        }
                                        sportDTO.setDisciplines(disciplineDTOList);
                                        return groupService.getAllByIdsList(groupIds).collectList().flatMap(ageGroups -> {
                                            List<DisciplineDTO> disciplineDTOS = sportDTO.getDisciplines();
                                            for(DisciplineDTO disciplineDTO : disciplineDTOS){
                                                for(AgeGroup ageGroup : ageGroups){
                                                    if(ageGroup.getDisciplineId() == disciplineDTO.getId()){
                                                        AgeGroupDTO ageGroupDTO = new AgeGroupDTO();
                                                        ageGroupDTO.setId(ageGroup.getId());
                                                        ageGroupDTO.setTitle(ageGroup.getTitle());
                                                        disciplineDTO.addAgeGroup(ageGroupDTO);
                                                    }
                                                }
                                            }
                                            sportDTO.setDisciplines(disciplineDTOS);
                                            return Mono.just(sportDTO);
                                        });
                                    });
                                })
                        )
                        .build()
        );
    }
}
