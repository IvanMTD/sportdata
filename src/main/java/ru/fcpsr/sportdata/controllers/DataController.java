package ru.fcpsr.sportdata.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.*;
import ru.fcpsr.sportdata.services.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/database")
public class DataController {

    private final TypeOfSportService sportService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;
    private final SubjectService subjectService;

    private final ParticipantService participantService;

    @GetMapping("/summary")
    public Mono<Rendering> getSummary(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Summary information")
                        .modelAttribute("index","data-sum-page")
                        .modelAttribute("form", getTotalStatistic())
                        .build()
        );
    }

    /**
     * УПРАВЛЕНИЕ БАЗОЙ ДАННЫХ - ВИДЫ СПОРТА
     * @param letter
     * @return
     */

    @GetMapping("/sport/{letter}")
    public Mono<Rendering> getSportData(@PathVariable(name = "letter") String letter){
        List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
        List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Sport data")
                        .modelAttribute("index","sport-data-page")
                        .modelAttribute("sports",getCompletedTypeOfSport(letter))
                        .modelAttribute("sportForm", new TypeOfSportDTO())
                        .modelAttribute("disciplineForm", new DisciplineDTO())
                        .modelAttribute("groupForm", new AgeGroupDTO())
                        .modelAttribute("filterForm", new FilterDTO())
                        .modelAttribute("filters", filters)
                        .modelAttribute("seasons", seasons)
                        .modelAttribute("letter",letter)
                        .build()
        );
    }

    @PostMapping("/sport/{letter}/add")
    public Mono<Rendering> addSport(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "sportForm") @Valid TypeOfSportDTO sportDTO, Errors errors){
        return sportService.findSportByTitle(sportDTO.getTitle()).flatMap(sport -> {
            errors.rejectValue("title","","Такой вид спорта уже есть");
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC, SportFilterType.NO_OLYMPIC, SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL, Season.SUMMER, Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title", "Sport data")
                            .modelAttribute("index", "sport-data-page")
                            .modelAttribute("sports", getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", sportDTO)
                            .modelAttribute("disciplineForm", new DisciplineDTO())
                            .modelAttribute("groupForm", new AgeGroupDTO())
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }).switchIfEmpty(Mono.just(sportDTO).flatMap(sport -> {
            if(errors.hasErrors()){
                List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC, SportFilterType.NO_OLYMPIC, SportFilterType.ADAPTIVE));
                List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL, Season.SUMMER, Season.WINTER));
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title", "Sport data")
                                .modelAttribute("index", "sport-data-page")
                                .modelAttribute("sports", getCompletedTypeOfSport(letter))
                                .modelAttribute("sportForm", sportDTO)
                                .modelAttribute("disciplineForm", new DisciplineDTO())
                                .modelAttribute("groupForm", new AgeGroupDTO())
                                .modelAttribute("filterForm", new FilterDTO())
                                .modelAttribute("filters", filters)
                                .modelAttribute("seasons", seasons)
                                .modelAttribute("letter", letter)
                                .build()
                );
            }

            return sportService.addNewSport(sport).flatMap(s -> {
                log.info("new sport saved: " + s.toString());
                return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
            });
        }));
    }

    @PostMapping("/sport/{letter}/filter/update")
    public Mono<Rendering> updateFilters(@ModelAttribute(name = "filterForm") FilterDTO filter, @PathVariable(name = "letter") String letter){
        return sportService.updateFilters(filter).flatMap(sport -> {
            log.info("sport filter update: " + sport.toString());
            return Mono.just(
                    Rendering.redirectTo("/database/sport/" + letter).build()
            );
        });
    }

    @PostMapping("/sport/{letter}/title/update")
    public Mono<Rendering> sportTitleUpdate(@ModelAttribute(name = "sportForm") @Valid TypeOfSportDTO sportDTO, Errors errors, @PathVariable(name = "letter") String letter){
        if(errors.hasErrors()){
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Sport data")
                            .modelAttribute("index","sport-data-page")
                            .modelAttribute("sports",getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", sportDTO)
                            .modelAttribute("disciplineForm", new DisciplineDTO())
                            .modelAttribute("groupForm", new AgeGroupDTO())
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }

        return sportService.updateSportTitle(sportDTO).flatMap(sport -> {
            log.info("sport title updated: " + sport.getTitle());
            return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
        });
    }

    @PostMapping("/sport/{letter}/discipline/add")
    public Mono<Rendering> addDiscipline(@ModelAttribute(name = "disciplineForm") @Valid DisciplineDTO disciplineDTO, Errors errors, @PathVariable(name = "letter") String letter){
        if(errors.hasErrors()){
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Sport data")
                            .modelAttribute("index","sport-data-page")
                            .modelAttribute("sports",getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", new TypeOfSportDTO())
                            .modelAttribute("disciplineForm", disciplineDTO)
                            .modelAttribute("groupForm", new AgeGroupDTO())
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }

        return disciplineService.addNew(disciplineDTO).flatMap(discipline -> {
            log.info("added new discipline: " + discipline.getId());
            return sportService.updateDisciplineInSport(discipline);
        }).flatMap(sport -> {
            log.info("added in sport new discipline: " + sport.getDisciplineIds());
            return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
        });
    }

    @PostMapping("/sport/{letter}/discipline/title/update")
    public Mono<Rendering> updateDisciplineTitle(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "disciplineForm") @Valid DisciplineDTO disciplineDTO, Errors errors){
        if(errors.hasErrors()){
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Sport data")
                            .modelAttribute("index","sport-data-page")
                            .modelAttribute("sports",getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", new TypeOfSportDTO())
                            .modelAttribute("disciplineForm", disciplineDTO)
                            .modelAttribute("groupForm", new AgeGroupDTO())
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }

        return disciplineService.updateTitle(disciplineDTO).flatMap(discipline -> {
            log.info("discipline title update: " + discipline.getTitle());
            return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
        });
    }

    @PostMapping("/sport/{letter}/discipline/group/add")
    public Mono<Rendering> addNewGroup(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "groupForm") @Valid AgeGroupDTO groupDTO, Errors errors){
        if(groupDTO.getMinAge() >= groupDTO.getMaxAge()){
            errors.rejectValue("minAge","","Минимальный возраст не может быть больше или равен максимальному!");
            errors.rejectValue("maxAge","","Максимальный возраст не может быть меньше или равен минимальному!");
        }
        if(errors.hasErrors()){
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Sport data")
                            .modelAttribute("index","sport-data-page")
                            .modelAttribute("sports",getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", new TypeOfSportDTO())
                            .modelAttribute("disciplineForm", new DisciplineDTO())
                            .modelAttribute("groupForm", groupDTO)
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }

        return groupService.addNewGroup(groupDTO).flatMap(group -> {
            log.info("group create: " + group.getId());
            return disciplineService.updateGroupInDiscipline(group).flatMap(discipline -> {
                log.info("discipline updated: " + discipline.getAgeGroupIds());
                return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
            });
        });
    }

    @PostMapping("/sport/{letter}/discipline/group/title/update")
    public Mono<Rendering> updateGroupTitle(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "groupForm") @Valid AgeGroupDTO groupDTO, Errors errors){
        if(errors.hasErrors()){
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Sport data")
                            .modelAttribute("index","sport-data-page")
                            .modelAttribute("sports",getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", new TypeOfSportDTO())
                            .modelAttribute("disciplineForm", new DisciplineDTO())
                            .modelAttribute("groupForm", groupDTO)
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }

        return groupService.updateTitle(groupDTO).flatMap(group -> {
            log.info("group title updated: " + group.getTitle());
            return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
        });
    }

    @PostMapping("/sport/{letter}/discipline/group/age/setup")
    public Mono<Rendering> setupAgeInAgeGroup(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "groupForm") @Valid AgeGroupDTO groupDTO, Errors errors){
        if(groupDTO.getMinAge() >= groupDTO.getMaxAge()){
            errors.rejectValue("minAge","","Минимальный возраст не может быть больше или равен максимальному!");
            errors.rejectValue("maxAge","","Максимальный возраст не может быть меньше или равен минимальному!");
        }
        if(errors.hasFieldErrors("minAge") || errors.hasFieldErrors("maxAge")){
            List<SportFilterType> filters = new ArrayList<>(Arrays.asList(SportFilterType.OLYMPIC,SportFilterType.NO_OLYMPIC,SportFilterType.ADAPTIVE));
            List<Season> seasons = new ArrayList<>(Arrays.asList(Season.ALL,Season.SUMMER,Season.WINTER));
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Sport data")
                            .modelAttribute("index","sport-data-page")
                            .modelAttribute("sports",getCompletedTypeOfSport(letter))
                            .modelAttribute("sportForm", new TypeOfSportDTO())
                            .modelAttribute("disciplineForm", new DisciplineDTO())
                            .modelAttribute("groupForm", groupDTO)
                            .modelAttribute("filterForm", new FilterDTO())
                            .modelAttribute("filters", filters)
                            .modelAttribute("seasons", seasons)
                            .modelAttribute("letter", letter)
                            .build()
            );
        }

        return groupService.updateAges(groupDTO).flatMap(group -> {
            log.info("group age has been updated: " + group.getMinAge() + " - " + group.getMaxAge());
            return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
        });
    }

    /**
     * УПРАВЛЕНИЕ БАЗОЙ ДАННЫХ - СУБЪЕКТЫ
     * @return
     */
    @GetMapping("/subject/{letter}")
    public Mono<Rendering> showSubjects(@PathVariable(name = "letter") String letter){
        Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
        Flux<Participant> participants = participantService.getAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);

        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Subject data")
                        .modelAttribute("index","subject-data-page")
                        .modelAttribute("subjects", getCompletedSubjects(letter))
                        .modelAttribute("sportList", sports)
                        .modelAttribute("pList", participants)
                        .modelAttribute("ssForm", new SubjectSportDTO())
                        .modelAttribute("letter",letter)
                        .build()
        );
    }

    @GetMapping("/{letter}/subject/{subjectId}/sport/delete/{sportId}")
    public Mono<Rendering> deleteSportFromSubject(@PathVariable(name = "letter") String letter, @PathVariable(name = "subjectId") int subjectId, @PathVariable(name = "sportId") int sportId){
        return subjectService.deleteSportFromSubject(subjectId,sportId).flatMap(subject -> {
            log.info("sport deleted from subject: " + subject.getTypeOfSportIds());
            return sportService.deleteSubjectFromSport(sportId,subject.getId()).flatMap(sport -> {
                log.info("subject deleted from sport: " + sport.getSubjectIds());
                return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
            });
        });
    }

    @PostMapping("/{letter}/subject/sport/add")
    public Mono<Rendering> addSportInSubject(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "ssForm") SubjectSportDTO ssDTO){
        return subjectService.addSportInSubject(ssDTO).flatMap(subject -> {
            log.info("sport added in subject: " + subject.getTypeOfSportIds());
            return sportService.addSubjectInSport(ssDTO);
        }).flatMap(sport -> {
            log.info("subject added in sport: " + sport.getSubjectIds());
            return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
        });
    }

    private Flux<SubjectDTO> getCompletedSubjects(String letter){
        return subjectService.getSubjectsByFirstLetter(letter).flatMap(subject -> {
            SubjectDTO subjectDTO = new SubjectDTO(subject);
            return sportService.findByIds(subject.getTypeOfSportIds()).collectList().flatMap(sports -> {
                List<TypeOfSportDTO> sportDTOList = new ArrayList<>();
                for(TypeOfSport sport : sports){
                    TypeOfSportDTO sportDTO = new TypeOfSportDTO(sport);
                    sportDTOList.add(sportDTO);
                }
                sportDTOList = sportDTOList.stream().sorted(Comparator.comparing(TypeOfSportDTO::getTitle)).collect(Collectors.toList());
                subjectDTO.setSports(sportDTOList);
                return participantService.findByIds(subject.getParticipantIds()).collectList();
            }).flatMap(participants -> {
                List<ParticipantDTO> participantDTOList = new ArrayList<>();
                for(Participant participant : participants){
                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                    participantDTOList.add(participantDTO);
                }
                participantDTOList = participantDTOList.stream().sorted(Comparator.comparing(ParticipantDTO::getFullName)).collect(Collectors.toList());
                subjectDTO.setParticipants(participantDTOList);
                return Mono.just(subjectDTO);
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    private Flux<TypeOfSportDTO> getCompletedTypeOfSport(String letter){
        return sportService.getSportsByFirstLetter(letter).flatMap(sport -> {
            TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
            return subjectService.getByIds(sport.getSubjectIds()).collectList().flatMap(subjects -> {
                List<SubjectDTO> subjectDTOList = new ArrayList<>();
                for(Subject subject : subjects){
                    SubjectDTO subjectDTO = new SubjectDTO(subject);
                    subjectDTOList.add(subjectDTO);
                }
                typeOfSportDTO.setSubjects(subjectDTOList);
                return Mono.just(typeOfSportDTO);
            }).flatMap(sportDTO -> disciplineService.getAllByIds(sport.getDisciplineIds()).collectList().flatMap(disciplines -> {
                List<DisciplineDTO> disciplineDTOList = new ArrayList<>();
                List<Integer> groupIds = new ArrayList<>();
                for(Discipline discipline : disciplines){
                    DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
                    disciplineDTOList.add(disciplineDTO);
                    groupIds.addAll(discipline.getAgeGroupIds());
                }
                sportDTO.setDisciplines(disciplineDTOList);
                return groupService.getAllByIdsList(groupIds).collectList().flatMap(ageGroups -> {
                    ageGroups = ageGroups.stream().sorted(Comparator.comparing(AgeGroup::getTitle)).collect(Collectors.toList());
                    List<DisciplineDTO> disciplineDTOS = sportDTO.getDisciplines();
                    for(DisciplineDTO disciplineDTO : disciplineDTOS){
                        for(AgeGroup ageGroup : ageGroups){
                            if(ageGroup.getDisciplineId() == disciplineDTO.getId()){
                                AgeGroupDTO ageGroupDTO = new AgeGroupDTO();
                                ageGroupDTO.setId(ageGroup.getId());
                                ageGroupDTO.setTitle(ageGroup.getTitle());
                                ageGroupDTO.setMinAge(ageGroup.getMinAge());
                                ageGroupDTO.setMaxAge(ageGroup.getMaxAge());
                                disciplineDTO.addAgeGroup(ageGroupDTO);
                            }
                        }
                    }
                    disciplineDTOS = disciplineDTOS.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
                    sportDTO.setDisciplines(disciplineDTOS);
                    return Mono.just(sportDTO);
                });
            }));
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(TypeOfSportDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    private Mono<FormDTO> getTotalStatistic(){
        return Mono.just(new FormDTO()).flatMap(form -> sportService.getAll().collectList().flatMap(sportList -> {
            form.setSportTotal(sportList.size());
            return disciplineService.getAll().collectList();
        }).flatMap(disciplines -> {
            form.setDisciplineTotal(disciplines.size());
            return groupService.getAll().collectList();
        }).flatMap(groupList -> {
            form.setGroupTotal(groupList.size());
            return subjectService.getAll().collectList();
        }).flatMap(subjects -> {
            form.setSubjectTotal(subjects.size());
            return participantService.getAll().collectList();
        }).flatMap(participants -> {
            form.setParticipantTotal(participants.size());
            return Mono.just(form);
        }));
    }
}
