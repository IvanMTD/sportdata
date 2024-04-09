package ru.fcpsr.sportdata.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/contest")
public class ContestController {

    private final TypeOfSportService sportService;
    private final DisciplineService disciplineService;
    private final QualificationService qualificationService;
    private final AgeGroupService groupService;
    private final ContestService contestService;
    private final ArchiveSportService archiveSportService;
    private final ParticipantService participantService;
    private final SportSchoolService schoolService;
    private final SubjectService subjectService;
    private final PlaceService placeService;

    private final BaseSportService baseSportService;

    @GetMapping("/first-step")
    public Mono<Rendering> firstStepPage(@RequestParam("contest") int contestId){
        return contestService.getById(contestId).flatMap(contest -> {
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","First step")
                            .modelAttribute("index","first-step-page")
                            .modelAttribute("contest", new ContestDTO(contest))
                            .modelAttribute("sports", sportService.getAll())
                            .modelAttribute("subjects", subjectService.getAll())
                            .build()
            );
        });
    }

    @PostMapping("/first-step")
    public Mono<Rendering> firstStepComplete(@ModelAttribute(name = "contest") @Valid ContestDTO contestDTO, Errors errors){
        return contestService.getContestByEkp(contestDTO.getEkp()).flatMap(contest -> {
            if(contest.getId() != contestDTO.getId()){
                if(contest.getId() != 0) {
                    if (contestDTO.getBeginning() != null) {
                        int year = contest.getBeginning().getYear();
                        int yearDTO = contestDTO.getBeginning().getYear();
                        if (year == yearDTO) {
                            errors.rejectValue("ekp", "", "Такой ЕКП уже зарегистрирован в системе!");
                        }
                    }
                }
            }
            if(errors.hasErrors()){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","First step")
                                .modelAttribute("index","first-step-page")
                                .modelAttribute("contest", contestDTO)
                                .modelAttribute("sports", sportService.getAll())
                                .modelAttribute("subjects", subjectService.getAll())
                                .build()
                );
            }

            if(contestDTO.getId() != 0){
                return contestService.updateContestFirstStep(contestDTO).flatMap(updatedContest -> {
                    log.info("step first done contest {} primary updated", updatedContest.getId());
                    return Mono.just(Rendering.redirectTo("/contest/second-step?contest=" + updatedContest.getId()).build());
                });
            }else {
                return contestService.createContestFirstStep(contestDTO).flatMap(savedContest -> {
                    log.info("step first done contest {} primary saved", savedContest.getId());
                    return Mono.just(Rendering.redirectTo("/contest/second-step?contest=" + savedContest.getId()).build());
                });
            }
        });
    }

    @GetMapping("/second-step")
    public Mono<Rendering> secondStepPage(@RequestParam(name = "contest") int contestId){
        return contestService.getById(contestId).flatMap(contest -> {
            log.info("contest {} found", contest.getId());
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Second step")
                            .modelAttribute("index","second-step-page")
                            .modelAttribute("contest", getCompleteContest(contest.getId()))
                            .modelAttribute("subjects", subjectService.getAll())
                            .build()
            );
        });
    }

    @PostMapping("/second-step")
    public Mono<Rendering> secondStepComplete(@ModelAttribute(name = "contest") ContestDTO contestDTO) {
        return contestService.updateContestSecondStep(contestDTO).flatMap(contest -> {
            log.info("contest {} updated", contest.getId());
            return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + contest.getId()).build());
        });
    }

    @GetMapping("/last-step")
    public Mono<Rendering> lastStepPage(@RequestParam(name = "contest") int contestId){
        return contestService.getById(contestId).flatMap(contest -> {
            log.info("contest {} found", contest.getId());
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Last step")
                            .modelAttribute("index","last-step-page")
                            .modelAttribute("contest", getCompleteContest(contestId))
                            .modelAttribute("sport", getCompleteSport(contest.getTypeOfSportId()))
                            .modelAttribute("participantList", getCompletedParticipantBySportId(contest.getTypeOfSportId()))
                            .modelAttribute("categories", getCategories())
                            .modelAttribute("federalStandards", getFedStandards())
                            .modelAttribute("conditions", getConditions())
                            .build()
            );
        });
    }

    @PostMapping("/last-step")
    public Mono<Rendering> lastStepPage(@ModelAttribute(name = "contest") ContestDTO contestDTO){
        log.info("DETECTED DATA: [{}]",contestDTO.getSports());
        for(SportDTO sportDTO : contestDTO.getSports()){
            for(PlaceDTO placeDTO : sportDTO.getPlaces()){
                log.info("PLACE IN DETECTED SPORTS: [{}]",placeDTO);
            }
        }
        return contestService.getById(contestDTO.getId()).flatMap(contest -> {
            log.info("FOUND CONTEST FULL INFO: [{}]", contest);
            return Flux.fromIterable(contestDTO.getSports()).flatMap(sportDTO -> {
                if(sportDTO.getDisciplineId() != 0) {
                    return archiveSportService.getById(sportDTO.getId()).flatMap(archiveSport -> {
                        archiveSport.setDisciplineId(sportDTO.getDisciplineId());
                        archiveSport.setAgeGroupId(sportDTO.getGroupId());
                        archiveSport.setContestId(contest.getId());
                        archiveSport.setupAllowed(sportDTO.getAllowed());
                        archiveSport.setupFederalStandards(sportDTO.getStandards());
                        return archiveSportService.saveArchiveSport(archiveSport).flatMap(preSavedSport -> {
                            return Flux.fromIterable(sportDTO.getPlaces()).flatMap(placeDTO -> {
                                if(placeDTO.getParticipantId() != 0) {
                                    return placeService.getById(placeDTO.getId()).flatMap(place -> {
                                        place.setParticipantId(placeDTO.getParticipantId());
                                        place.setSportSchoolId(placeDTO.getSchoolId());
                                        place.setQualificationId(placeDTO.getQualificationId());
                                        place.setPlace(placeDTO.getPlace());
                                        place.setCondition(placeDTO.getCondition());
                                        place.setASportId(preSavedSport.getId());
                                        place.setResultCategory(placeDTO.getNewQualificationData());
                                        return placeService.setPlace(place).flatMap(savedPlace -> {
                                            if (savedPlace.getCondition().equals(Condition.ALLOW)) {
                                                return Mono.just(savedPlace);
                                            } else {
                                                int pid = savedPlace.getParticipantId();
                                                int sid = contestDTO.getSportId();
                                                Qualification qualification = new Qualification();
                                                qualification.setParticipantId(pid);
                                                qualification.setTypeOfSportId(sid);
                                                qualification.setCategory(placeDTO.getNewQualificationData());
                                                return qualificationService.save(qualification).flatMap(q -> {
                                                    return participantService.addQualificationToParticipant(q).flatMap(p -> {
                                                        return sportService.addQualificationInSport(q).flatMap(s -> {
                                                            place.setNewQualificationId(q.getId());
                                                            return placeService.setPlace(place);
                                                        });
                                                    });
                                                });
                                            }
                                        });
                                    });
                                }else{
                                    return Mono.empty();
                                }
                            }).collectList().flatMap(pl -> {
                                Set<Integer> ids = new HashSet<>();
                                for (Place place : pl) {
                                    ids.add(place.getId());
                                }
                                preSavedSport.setPlaceIds(ids);
                                return archiveSportService.saveArchiveSport(preSavedSport);
                            });
                        });
                    });
                }else{
                    return Mono.empty();
                }
            }).collectList().flatMap(archiveSports -> {
                Set<Integer> ids = new HashSet<>();
                for(ArchiveSport archiveSport : archiveSports){
                    ids.add(archiveSport.getId());
                }
                contest.setASportIds(ids);
                if(contestDTO.isComplete()){
                    contest.setComplete(true);
                }
                return contestService.saveContest(contest);
            }).flatMap(savedContest -> {
                log.info("contest fullish updated {}", savedContest);
                if(savedContest.isComplete()){
                    return Mono.just(Rendering.redirectTo("/").build());
                }else{
                    return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + savedContest.getId()).build());
                }
            });
        });
    }

    private Flux<ParticipantDTO> getCompletedParticipantBySportId(int sportId) {
        return qualificationService.getAllBySportId(sportId).flatMap(qualification -> {
            QualificationDTO qualificationDTO = new QualificationDTO(qualification);
            return sportService.getById(qualification.getTypeOfSportId()).flatMap(sport -> {
                TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                qualificationDTO.setSport(typeOfSportDTO);
                return participantService.getById(qualification.getParticipantId()).flatMap(participant -> {
                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                    participantDTO.addQualification(qualificationDTO);
                    participantDTO.setSchoolIds(participant.getSportSchoolIds());
                    return Mono.just(participantDTO);
                });
            });
        }).flatMap(participantDTO -> schoolService.getAllByIdIn(participantDTO.getSchoolIds()).flatMap(school -> {
            SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
            return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                SubjectDTO subjectDTO = new SubjectDTO(subject);
                sportSchoolDTO.setSubject(subjectDTO);
                return Mono.just(sportSchoolDTO);
            });
        }).collectList().flatMap(sl -> {
            sl = sl.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
            participantDTO.setSchools(sl);
            return Mono.just(participantDTO);
        })).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(ParticipantDTO::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }


    @GetMapping("/new")
    public Mono<Rendering> addContestPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Add contest page")
                        .modelAttribute("index","add-contest-page")
                        .modelAttribute("contestForm", new ContestDTO())
                        .modelAttribute("sports", getSports())
                        .modelAttribute("subjects", subjectService.getAll())
                        .modelAttribute("categories", getCategories())
                        .modelAttribute("federalStandards", getFedStandards())
                        .modelAttribute("conditions", getConditions())
                        .build()
        );
    }

    @PostMapping("/add")
    public Mono<Rendering> addContest(@ModelAttribute(name = "contestForm") @Valid ContestDTO contestDTO, Errors errors){
       /* return Mono.just(Rendering.redirectTo("/contest/new").build());*/
        return contestService.getContestByEkp(contestDTO.getEkp()).flatMap(c -> {
            if(errors.hasErrors()){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Add contest page")
                                .modelAttribute("index","add-contest-page")
                                .modelAttribute("contestForm", contestDTO)
                                .modelAttribute("sports", getSports())
                                .modelAttribute("subjects", subjectService.getAll())
                                .modelAttribute("categories", getCategories())
                                .modelAttribute("federalStandards", getFedStandards())
                                .modelAttribute("conditions", getConditions())
                                .build()
                );
            }
            if(c.getId() != 0){
                if(c.getEkp().equals(contestDTO.getEkp())){
                    int year1 = c.getBeginning().getYear();
                    int year2 = contestDTO.getBeginning().getYear();
                    if(year1 == year2){
                        errors.rejectValue("ekp","","Такой номер ЕКП и год проведения уже зарегистрирован. Это ошибка!");
                        return Mono.just(
                                Rendering.view("template")
                                        .modelAttribute("title","Add contest page")
                                        .modelAttribute("index","add-contest-page")
                                        .modelAttribute("contestForm", contestDTO)
                                        .modelAttribute("sports", getSports())
                                        .modelAttribute("subjects", subjectService.getAll())
                                        .modelAttribute("categories", getCategories())
                                        .modelAttribute("federalStandards", getFedStandards())
                                        .modelAttribute("conditions", getConditions())
                                        .build()
                        );
                    }
                }
            }

            return contestService.addContest(contestDTO).flatMap(contest -> {
                log.info("contest saved: " + contest.toString());
                return Flux.fromIterable(contestDTO.getSports()).flatMap(sport -> {
                    if(sport.getDisciplineId() == 0){
                        return Mono.empty();
                    }else {
                        System.out.println(sport);
                        ArchiveSport archiveSport = new ArchiveSport(sport);
                        archiveSport.setContestId(contest.getId());
                        return archiveSportService.saveArchiveSport(archiveSport).flatMap(as -> {
                            log.info("aSport saved: " + as.toString());
                            return Flux.fromIterable(sport.getPlaces()).flatMap(placeDTO -> {
                                if (placeDTO.getParticipantId() == 0) {
                                    return Mono.empty();
                                } else {
                                    System.out.println(placeDTO.toString());
                                    Place place = new Place(placeDTO);
                                    place.setASportId(as.getId());
                                    return qualificationService.getById(placeDTO.getQualificationId()).flatMap(q -> {
                                        System.out.println(q.toString());
                                        if(!q.getCategory().equals(placeDTO.getNewQualificationData())){
                                            Qualification qualification = new Qualification();
                                            qualification.setCategory(placeDTO.getNewQualificationData());
                                            qualification.setTypeOfSportId(contestDTO.getSportId());
                                            qualification.setParticipantId(placeDTO.getParticipantId());
                                            return qualificationService.save(qualification).flatMap(newQualification -> {
                                                place.setNewQualificationId(qualification.getId());
                                                return placeService.setPlace(place);
                                            });
                                        }
                                        place.setNewQualificationId(q.getId());
                                        return placeService.setPlace(place);
                                    });
                                }
                            }).collectList().flatMap(pl -> {
                                for (Place place : pl) {
                                    as.addPlace(place);
                                }
                                return archiveSportService.saveArchiveSport(as);
                            });
                        });
                    }
                }).collectList().flatMap(sl -> {
                    for(ArchiveSport archiveSport : sl){
                        contest.addArchiveSport(archiveSport);
                    }
                    return contestService.saveContest(contest);
                });
            }).flatMap(contest -> {
                log.info("contest fullish created " + contest.toString());
                return Mono.just(Rendering.redirectTo("/contest/new").build());
            });
        });
    }

    @GetMapping("/get/all")
    public Mono<Rendering> getAll(@RequestParam(name = "page") int page){
        System.out.println(page);
        return contestService.getCount().flatMap(count -> {
            int pageControl = page;
            if(pageControl < 0){
                pageControl = 0;
            }
            long lastPage = count / 12;
            if(pageControl >= lastPage){
                pageControl = (int)lastPage;
            }
            System.out.println(pageControl);
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title", "Contest information")
                            .modelAttribute("index","contest-all-page")
                            .modelAttribute("contests", getAllCompleteContest(PageRequest.of(pageControl,12)))
                            .modelAttribute("page",pageControl)
                            .modelAttribute("lastPage", lastPage)
                            .build()
            );
        });
    }

    private Mono<TypeOfSportDTO> getCompleteSport(int sportId) {
        return sportService.findById(sportId).flatMap(sport -> {
            TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
            return disciplineService.getAllByIds(sport.getDisciplineIds()).flatMap(discipline -> {
                DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
                return Mono.just(disciplineDTO);
            }).collectList().flatMap(dl -> {
                dl = dl.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
                typeOfSportDTO.setDisciplines(dl);
                return Mono.just(typeOfSportDTO);
            }).flatMap(sportDTO -> {
                log.info("try found groups");
                return groupService.getAllByIds2(sport.getAgeGroupIds()).collectList().flatMap(gl -> {
                    gl = gl.stream().sorted(Comparator.comparing(AgeGroup::getTitle)).collect(Collectors.toList());
                    List<AgeGroupDTO> ageGroupDTOS = new ArrayList<>();
                    for(AgeGroup ageGroup : gl){
                        AgeGroupDTO ageGroupDTO = new AgeGroupDTO(ageGroup);
                        ageGroupDTOS.add(ageGroupDTO);
                    }
                    sportDTO.setGroups(ageGroupDTOS);
                    return Mono.just(sportDTO);
                });
            });
        });
    }

    private Flux<ContestDTO> getAllCompleteContest(Pageable pageable) {
        return contestService.getAllSortedByDate(pageable).flatMap(contest -> {
            log.info("FOUND DATA IN DB: [{}}", contest);
            return getCompleteContest(contest.getId());
        });
    }

    private Mono<ContestDTO> getCompleteContest(int contestId){
        return contestService.getById(contestId).flatMap(contest -> {
            ContestDTO contestDTO = new ContestDTO(contest);
            return subjectService.getAll().collectList().flatMap(subjects -> {
                for(Subject subject : subjects){
                    if(subject.getId() == contest.getSubjectId()){
                        contestDTO.setSubject(new SubjectDTO(subject));
                    }
                    if(contest.getTotalSubjects().stream().anyMatch(sid -> sid == subject.getId())){
                        contestDTO.addSubjectInTotal(new SubjectDTO(subject));
                    }
                    if(contest.getFirstPlace().stream().anyMatch(sid -> sid == subject.getId())){
                        contestDTO.addSubjectInFirst(new SubjectDTO(subject));
                    }
                    if(contest.getSecondPlace().stream().anyMatch(sid -> sid == subject.getId())){
                        contestDTO.addSubjectInSecond(new SubjectDTO(subject));
                    }
                    if(contest.getLastPlace().stream().anyMatch(sid -> sid == subject.getId())){
                        contestDTO.addSubjectInLast(new SubjectDTO(subject));
                    }
                }
                log.info("aSport size is {}", contest.getASportIds());
                return archiveSportService.getAllByIdIn(contest.getASportIds()).flatMap(archiveSport -> {
                    log.info("found a sport {}", archiveSport);
                    SportDTO sportDTO = new SportDTO(archiveSport);
                    return sportService.getById(contest.getTypeOfSportId()).flatMap(sport -> {
                        contestDTO.setSport(new TypeOfSportDTO(sport));
                        if(sportDTO.getId() == 0){
                            return Mono.just(sportDTO);
                        }else {
                            return disciplineService.getById(archiveSport.getDisciplineId()).flatMap(discipline -> {
                                sportDTO.setDiscipline(new DisciplineDTO(discipline));
                                return groupService.getById(archiveSport.getAgeGroupId()).flatMap(ageGroup -> {
                                    sportDTO.setGroup(new AgeGroupDTO(ageGroup));
                                    return placeService.getAllByIdIn(archiveSport.getPlaceIds()).flatMap(place -> {
                                        PlaceDTO placeDTO = new PlaceDTO(place);
                                        return participantService.getById(place.getParticipantId()).flatMap(participant -> {
                                            placeDTO.setParticipant(new ParticipantDTO(participant));
                                            return qualificationService.getById(place.getQualificationId()).flatMap(qualification -> {
                                                placeDTO.setQualification(new QualificationDTO(qualification));
                                                return schoolService.getById(place.getSportSchoolId()).flatMap(school -> {
                                                    SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                                                    return subjectService.getById(school.getSubjectId()).flatMap(subject2 -> {
                                                        sportSchoolDTO.setSubject(new SubjectDTO(subject2));
                                                        placeDTO.setSchool(sportSchoolDTO);
                                                        return Mono.just(placeDTO);
                                                    });
                                                });
                                            });
                                        });
                                    }).collectList().flatMap(placesDTO -> {
                                        placesDTO = placesDTO.stream().sorted(Comparator.comparing(PlaceDTO::getPlace)).collect(Collectors.toList());
                                        sportDTO.setPlaces(placesDTO);
                                        return Mono.just(sportDTO);
                                    });
                                });
                            });
                        }
                    });
                }).collectList().flatMap(sportDTOS -> {
                    log.info("data.size {}", sportDTOS.size());
                    if(sportDTOS.size() == 1){
                        if(sportDTOS.get(0).getId() == 0){
                            contestDTO.setSports(sportDTOS);
                            System.out.println(sportDTOS);
                            return Mono.just(contestDTO);
                        }else{
                            sportDTOS = sportDTOS.stream().sorted(Comparator.comparing(sportDTO -> sportDTO.getDiscipline().getTitle())).collect(Collectors.toList());
                            if(!contest.isComplete()) {
                                sportDTOS.add(new SportDTO());
                            }
                            System.out.println(sportDTOS);
                            contestDTO.setSports(sportDTOS);
                            return sportService.getById(contest.getTypeOfSportId()).flatMap(sport -> {
                                return baseSportService.getAllByIds(sport.getBaseSportIds()).flatMap(baseSport -> {
                                    return subjectService.getById(baseSport.getSubjectId()).flatMap(subject -> {
                                        return Mono.just(new SubjectDTO(subject));
                                    });
                                }).collectList().flatMap(subjectsDTO -> {
                                    contestDTO.setBaseSubjectTotal(subjectsDTO);
                                    contestDTO.calcBaseIn();
                                    return Mono.just(contestDTO);
                                });
                            });
                        }
                    }else{
                        sportDTOS = sportDTOS.stream().sorted(Comparator.comparing(sportDTO -> sportDTO.getDiscipline().getTitle())).collect(Collectors.toList());
                        if(!contest.isComplete()) {
                            sportDTOS.add(new SportDTO());
                        }
                        System.out.println(sportDTOS);
                        contestDTO.setSports(sportDTOS);
                        return sportService.getById(contest.getTypeOfSportId()).flatMap(sport -> {
                            return baseSportService.getAllByIds(sport.getBaseSportIds()).flatMap(baseSport -> {
                                return subjectService.getById(baseSport.getSubjectId()).flatMap(subject -> {
                                    return Mono.just(new SubjectDTO(subject));
                                });
                            }).collectList().flatMap(subjectsDTO -> {
                                contestDTO.setBaseSubjectTotal(subjectsDTO);
                                contestDTO.calcBaseIn();
                                return Mono.just(contestDTO);
                            });
                        });
                    }
                });
            });
        });
    }

    /*private Flux<ContestDTO> getAllCompletedContest(){
        return contestService.getAll().flatMap(contest -> {
            ContestDTO contestDTO = new ContestDTO(contest);
            return subjectService.getAll().collectList().flatMap(subjects -> {
                for(Subject subject : subjects){
                    if(subject.getId() == contest.getSubjectId()){
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        contestDTO.setSubject(subjectDTO);
                    }
                    if(contest.getTotalSubjects().stream().anyMatch(sid -> sid == subject.getId())){
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        contestDTO.addSubjectInTotal(subjectDTO);
                    }
                    if(contest.getFirstPlace().stream().anyMatch(sid -> sid == subject.getId())){
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        contestDTO.addSubjectInFirst(subjectDTO);
                    }
                    if(contest.getSecondPlace().stream().anyMatch(sid -> sid == subject.getId())){
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        contestDTO.addSubjectInSecond(subjectDTO);
                    }
                    if(contest.getLastPlace().stream().anyMatch(sid -> sid == subject.getId())){
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        contestDTO.addSubjectInLast(subjectDTO);
                    }
                }
                return archiveSportService.getAllByIdIn(contest.getASportIds()).flatMap(archiveSport -> {
                    SportDTO sportDTO = new SportDTO();
                    sportDTO.setStandard(archiveSport.getFederalStandard());
                    List<Category> categories = new ArrayList<>(archiveSport.getAllowed());
                    sportDTO.setAllowed(categories);
                    return sportService.getById(archiveSport.getTypeOfSportId()).flatMap(sport -> {
                        TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                        sportDTO.setSport(typeOfSportDTO);
                        return disciplineService.getById(archiveSport.getDisciplineId()).flatMap(discipline -> {
                            DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
                            sportDTO.setDiscipline(disciplineDTO);
                            return groupService.getById(archiveSport.getAgeGroupId()).flatMap(ageGroup -> {
                                AgeGroupDTO ageGroupDTO = new AgeGroupDTO(ageGroup);
                                sportDTO.setGroup(ageGroupDTO);
                                return placeService.getAllByIdIn(archiveSport.getPlaceIds()).flatMap(place -> {
                                    PlaceDTO placeDTO = new PlaceDTO();
                                    placeDTO.setPlace(place.getPlace());
                                    placeDTO.setCondition(place.getCondition());
                                    return participantService.getById(place.getParticipantId()).flatMap(participant -> {
                                        ParticipantDTO participantDTO = new ParticipantDTO(participant);
                                        placeDTO.setParticipant(participantDTO);
                                        return qualificationService.getById(place.getQualificationId()).flatMap(qualification -> {
                                            QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                                            placeDTO.setQualification(qualificationDTO);
                                            return schoolService.getById(place.getSportSchoolId()).flatMap(school -> {
                                                SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                                                return subjectService.getById(school.getSubjectId()).flatMap(subject2 -> {
                                                    SubjectDTO subjectDTO2 = new SubjectDTO(subject2);
                                                    sportSchoolDTO.setSubject(subjectDTO2);
                                                    placeDTO.setSchool(sportSchoolDTO);
                                                    return Mono.just(placeDTO);
                                                });
                                            });
                                        });
                                    });
                                }).collectList().flatMap(placesDTO -> {
                                    placesDTO = placesDTO.stream().sorted(Comparator.comparing(PlaceDTO::getPlace)).collect(Collectors.toList());
                                    sportDTO.setPlaces(placesDTO);
                                    return Mono.just(sportDTO);
                                });
                            });
                        });
                    });
                }).collectList().flatMap(sportDTOS -> {
                    sportDTOS = sportDTOS.stream().sorted(Comparator.comparing(sportDTO -> sportDTO.getDiscipline().getTitle())).collect(Collectors.toList());
                    contestDTO.setSports(sportDTOS);
                    return sportService.getById(sportDTOS.get(0).getSport().getId()).flatMap(sport -> baseSportService.getAllByIds(sport.getBaseSportIds()).flatMap(bs -> subjectService.getById(bs.getSubjectId()).flatMap(s -> {
                        SubjectDTO subjectDTO = new SubjectDTO(s);
                        return Mono.just(subjectDTO);
                    })).collectList().flatMap(sl -> {
                        contestDTO.setBaseSubjectTotal(sl);
                        contestDTO.calcBaseIn();
                        return Mono.just(contestDTO);
                    }));
                });
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(ContestDTO::getId)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }*/

    private Flux<TypeOfSportDTO> getSports(){
        return sportService.getAll().flatMap(sport -> {
            TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
            return disciplineService.getAllByIds(sport.getDisciplineIds()).flatMap(discipline -> {
                DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
                return Mono.just(disciplineDTO);
            }).collectList().flatMap(dl -> {
                dl = dl.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
                typeOfSportDTO.setDisciplines(dl);
                return groupService.getAllByIds(sport.getAgeGroupIds()).flatMap(group -> {
                    AgeGroupDTO ageGroupDTO = new AgeGroupDTO(group);
                    return Mono.just(ageGroupDTO);
                }).collectList().flatMap(gl -> {
                    gl = gl.stream().sorted(Comparator.comparing(AgeGroupDTO::getTitle)).collect(Collectors.toList());
                    typeOfSportDTO.setGroups(gl);
                    return Mono.just(typeOfSportDTO);
                });
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(TypeOfSportDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    private List<ConditionDTO> getConditions(){
        List<ConditionDTO> cl = new ArrayList<>();
        for(Condition condition : Condition.values()){
            cl.add(new ConditionDTO(condition));
        }
        return cl;
    }

    private List<FederalStandardDTO> getFedStandards(){
        List<FederalStandardDTO> fsl = new ArrayList<>();
        for(FederalStandard federalStandard : FederalStandard.values()){
            fsl.add(new FederalStandardDTO(federalStandard));
        }
        return fsl;
    }

    private List<CategoryDTO> getCategories(){
        List<CategoryDTO> cl = new ArrayList<>();
        for(Category category : Category.values()){
            cl.add(new CategoryDTO(category));
        }
        return cl;
    }
}
