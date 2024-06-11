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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.enums.Category;
import ru.fcpsr.sportdata.enums.Condition;
import ru.fcpsr.sportdata.enums.FederalStandard;
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

    private final NewsService newsService;

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
    public Mono<Rendering> firstStepComplete(@ModelAttribute(name = "contest") @Valid ContestDTO contestDTO, Errors errors, ServerWebExchange exchange){
        return exchange.getFormData().flatMap(form -> {
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

                int save = Integer.parseInt(form.get("save").get(0));

                if(contestDTO.getId() != 0){
                    return contestService.updateContestFirstStep(contestDTO).flatMap(updatedContest -> {
                        log.info("step first done contest {} primary updated", updatedContest.getId());
                        if(save == 0) {
                            return Mono.just(Rendering.redirectTo("/contest/second-step?contest=" + updatedContest.getId()).build());
                        }else{
                            return Mono.just(Rendering.redirectTo("/contest/first-step?contest=" + updatedContest.getId()).build());
                        }
                    });
                }else {
                    return contestService.createContestFirstStep(contestDTO).flatMap(savedContest -> {
                        log.info("step first done contest {} primary saved", savedContest.getId());
                        if(save == 0) {
                            return Mono.just(Rendering.redirectTo("/contest/second-step?contest=" + savedContest.getId()).build());
                        }else{
                            return Mono.just(Rendering.redirectTo("/contest/first-step?contest=" + savedContest.getId()).build());
                        }
                    });
                }
            });
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
                            .modelAttribute("contest", getCompleteContest(contest.getId(),0))
                            .modelAttribute("subjects", subjectService.getAll())
                            .build()
            );
        });
    }

    @PostMapping("/second-step")
    public Mono<Rendering> secondStepComplete(@ModelAttribute(name = "contest") ContestDTO contestDTO, ServerWebExchange exchange) {
        return exchange.getFormData().flatMap(form -> {
            return contestService.updateContestSecondStep(contestDTO).flatMap(contest -> {
                int save = Integer.parseInt(form.get("save").get(0));
                log.info("contest {} updated", contest.getId());
                if(save == 0) {
                    return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + contest.getId()).build());
                }else{
                    return Mono.just(Rendering.redirectTo("/contest/second-step?contest=" + contest.getId()).build());
                }
            });
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
                            .modelAttribute("contest", getCompleteContest(contestId,0))
                            .modelAttribute("sport", getCompleteSport(contest.getTypeOfSportId()))
                            .modelAttribute("categories", getCategories())
                            .modelAttribute("federalStandards", getFedStandards())
                            .modelAttribute("conditions", getConditions())
                            .build()
            );
        });
    }

    @PostMapping("/last-step")
    public Mono<Rendering> lastStepPage(ServerWebExchange exchange, @ModelAttribute(name = "contest") ContestDTO contestDTO){
        return exchange.getFormData().flatMap(form -> {
            //log.info("DETECTED DATA: [{}]",contestDTO);
        /*for(SportDTO sportDTO : contestDTO.getSports()){
            for(PlaceDTO placeDTO : sportDTO.getPlaces()){
                log.info("PLACE IN DETECTED SPORTS: [{}]",placeDTO);
            }
        }*/
            return contestService.getById(contestDTO.getId()).flatMap(contest -> {
                //log.info("1.FOUND CONTEST FULL INFO: [{}]", contest);
                return Flux.fromIterable(contestDTO.getSports()).flatMap(sportDTO -> {
                    //log.info("2.INNER SPORT DTO [{}]", sportDTO);
                    if(sportDTO.getDisciplineId() != 0) {
                        return archiveSportService.getById(sportDTO.getId()).flatMap(archiveSport -> {
                            //log.info("3.FOUND ARCHIVE SPORT [{}]", archiveSport);
                            archiveSport.setDisciplineId(sportDTO.getDisciplineId());
                            archiveSport.setAgeGroupId(sportDTO.getGroupId());
                            archiveSport.setContestId(contest.getId());
                            archiveSport.setupAllowed(sportDTO.getAllowed());
                            archiveSport.setupFederalStandards(sportDTO.getStandards());
                            return archiveSportService.saveArchiveSport(archiveSport).flatMap(preSavedSport -> {
                                return Flux.fromIterable(sportDTO.getPlaces()).flatMap(placeDTO -> {
                                    //log.info("4.INNER PLACE DTO [{}]", placeDTO);
                                    if(placeDTO.getParticipantId() != 0) {
                                        return placeService.getById(placeDTO.getId()).flatMap(place -> {
                                            //log.info("5.FOUND PLACE [{}]", place);
                                            place.setParticipantId(placeDTO.getParticipantId());
                                            place.setSportSchoolId(placeDTO.getSchoolId());
                                            place.setQualificationId(placeDTO.getQualificationId());
                                            place.setPlace(placeDTO.getPlace());
                                            place.setCondition(placeDTO.getCondition());
                                            place.setASportId(preSavedSport.getId());
                                            place.setResultCategory(placeDTO.getNewQualificationData());
                                            place.setParallelSchoolId(placeDTO.getParallelSchoolId());
                                            place.setInfo(placeDTO.getInfo());
                                            return placeService.setPlace(place).flatMap(savedPlace -> {
                                                if (!savedPlace.getCondition().equals(Condition.DONE)) {
                                                    return Mono.just(savedPlace);
                                                } else {
                                                    int pid = savedPlace.getParticipantId();
                                                    int sid = contest.getTypeOfSportId();
                                                    Qualification qualification = new Qualification();
                                                    qualification.setParticipantId(pid);
                                                    qualification.setTypeOfSportId(sid);
                                                    qualification.setCategory(placeDTO.getNewQualificationData());
                                                    return qualificationService.getBySportIdAndParticipantIdAndCategory(qualification).flatMap(checkQ -> {
                                                        if(checkQ.getId() == 0) {
                                                            return qualificationService.save(qualification).flatMap(q -> {
                                                                //log.info("EXTRA: SAVED QUALIFICATION [{}]",q);
                                                                return participantService.addQualificationToParticipant(q).flatMap(p -> {
                                                                    //log.info("EXTRA: UPDATED PARTICIPANT [{}]",p);
                                                                    return sportService.addQualificationInSport(q).flatMap(s -> {
                                                                        //log.info("EXTRA: UPDATED SPORT [{}]",s);
                                                                        place.setNewQualificationId(q.getId());
                                                                        return placeService.setPlace(place);
                                                                    });
                                                                });
                                                            });
                                                        }else{
                                                            return Mono.just(place);
                                                        }
                                                    });
                                                }
                                            });
                                        });
                                    }else{
                                        return Mono.empty();
                                    }
                                }).collectList().flatMap(pl -> {
                                    Set<Long> ids = new HashSet<>();
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
                    Set<Long> ids = new HashSet<>();
                    for(ArchiveSport archiveSport : archiveSports){
                        ids.add(archiveSport.getId());
                    }
                    contest.setASportIds(ids);
                    contest.setComplete(contestDTO.isComplete());
                    return contestService.saveContest(contest);
                }).flatMap(savedContest -> {
                    //log.info("contest fullish updated {}", savedContest);
                    int added = Integer.parseInt(form.get("added").get(0));
                    int save = Integer.parseInt(form.get("save").get(0));
                    if(added == 0){
                        if(save == 0) {
                            if (savedContest.isComplete()) {
                                return Mono.just(Rendering.redirectTo("/contest/get/all?page=0&search=all").build());
                            } else {
                                return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + savedContest.getId()).build());
                            }
                        }else{
                            return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + savedContest.getId()).build());
                        }
                    }else{
                        return Mono.just(
                                Rendering.view("template")
                                        .modelAttribute("title","Last step")
                                        .modelAttribute("index","last-step-page")
                                        .modelAttribute("contest", getCompleteContest(contest.getId(), added))
                                        .modelAttribute("sport", getCompleteSport(contest.getTypeOfSportId()))
                                        .modelAttribute("categories", getCategories())
                                        .modelAttribute("federalStandards", getFedStandards())
                                        .modelAttribute("conditions", getConditions())
                                        .build()
                        );
                    }
                });
            });
        });
    }

    @GetMapping("/last-step/discipline/delete")
    public Mono<Rendering> disciplineDeleteFromLastStep(@RequestParam(name = "discipline") int did, @RequestParam(name = "contest") int cid){
        if(did != 0) {
            return archiveSportService.deleteBy(did).flatMap(deleted -> {
                log.info("discipline has been deleted [{}]", deleted);
                return placeService.deleteAllCurrent(deleted.getPlaceIds()).collectList().flatMap(l -> {
                    for (Place place : l) {
                        log.info("place has been deleted [{}]", place);
                    }
                    return contestService.deleteArchiveSportFromContest(deleted).flatMap(contest -> {
                        log.info("discipline has been removed from contest [{}]", contest);
                        if(contest.getASportIds().size() == 0){
                            contest.setComplete(false);
                            return contestService.saveContest(contest).flatMap(saved -> {
                                log.info("contest has been updated [{}]",saved);
                                return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + contest.getId()).build());
                            });
                        }else{
                            return Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + contest.getId()).build());
                        }
                    });
                });
            });
        }else{
            return contestService.getById(cid).flatMap(contest -> Mono.just(Rendering.redirectTo("/contest/last-step?contest=" + contest.getId()).build()));
        }
    }

    @GetMapping("/monitor")
    public Mono<Rendering> monitorData(@RequestParam(name = "monitor") int id){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Monitor page")
                        .modelAttribute("index","monitor-page")
                        .modelAttribute("contest", getCompleteContest(id,0))
                        .build()
        );
    }

    @GetMapping("/search/{page}")
    public Mono<Rendering> search(@PathVariable(name = "page") int page, @RequestParam(name = "search") String search){
        return Mono.just(Rendering.redirectTo("/contest/get/all?page=" + page + "&search=" + search.replace(" ", "+")).build());
    }

    @GetMapping("/get/all")
    public Mono<Rendering> getAll(@RequestParam(name = "page") int page, @RequestParam(name = "search") String search){
        return contestService.getCountBy(search).flatMap(count -> {
            int pageSize = 12;

            int pageControl = page;
            if(pageControl < 0){
                pageControl = 0;
            }
            long lastPage = count / pageSize;
            if(count == pageSize){
                lastPage = 0;
            }
            if(pageControl >= lastPage){
                pageControl = (int)lastPage;
            }
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title", "Contest information")
                            .modelAttribute("index","contest-all-page")
                            .modelAttribute("contests", getAllCompleteContest(PageRequest.of(pageControl,pageSize),search))
                            .modelAttribute("page",pageControl)
                            .modelAttribute("lastPage", lastPage)
                            .modelAttribute("search",search)
                            .build()
            );
        });
    }

    @GetMapping("/show")
    public Mono<Rendering> showContestDetails(@RequestParam(name = "contest") int contestId){
        return contestService.getById(contestId).flatMap(contest -> {
            log.info("contest found {}", contest);
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Contest page")
                            .modelAttribute("index","contest-detail-page")
                            .modelAttribute("contest", getCompleteContest(contest.getId(),0))
                            .modelAttribute("categories", getCategories())
                            .modelAttribute("federalStandards", getFedStandards())
                            .modelAttribute("conditions", getConditions())
                            .build()
            );
        });
    }

    @GetMapping("/delete")
    public Mono<Rendering> deleteContest(@RequestParam(name = "contest") int contestId){
        return contestService.deleteContest(contestId).flatMap(deletedContest -> {
            log.info("CONTEST HAS BEEN DELETED: [{}]", deletedContest);
            return archiveSportService.deleteAllCurrent(deletedContest.getASportIds()).flatMap(deletedArchiveSport -> {
                log.info("ARCHIVE SPORT DELETED: [{}]", deletedArchiveSport);
                return placeService.deleteAllCurrent(deletedArchiveSport.getPlaceIds());
            }).collectList();
        }).flatMap(l -> {
            for(Place place : l){
                log.info("PLACE HAS BEEN DELETED: [{}]", place);
            }
            return Mono.just(Rendering.redirectTo("/contest/get/all?page=0&search=all").build());
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

    private Flux<ContestDTO> getAllCompleteContest(Pageable pageable, String search) {
        if(search.equals("all") || search.equals("")) {
            return contestService.getAllSortedBySportTitle(pageable).flatMap(contest -> {
                return getCompleteContest(contest.getId(),0);
            }).collectList().flatMapMany(cl -> {
                cl = cl.stream().sorted(Comparator.comparing(ContestDTO::getSportTitle)).collect(Collectors.toList());
                return Flux.fromIterable(cl);
            }).flatMapSequential(Mono::just);
        }else{
            return contestService.getAllBy(pageable,search).flatMap(contest -> {
                return getCompleteContest(contest.getId(),0);
            }).collectList().flatMapMany(cl -> {
                cl = cl.stream().sorted(Comparator.comparing(ContestDTO::getSportTitle)).collect(Collectors.toList());
                return Flux.fromIterable(cl);
            }).flatMapSequential(Mono::just);
        }
    }

    private Mono<ContestDTO> getCompleteContest(long contestId, int added){
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
                //log.info("aSport size is {}", contest.getASportIds());
                return archiveSportService.getAllByIdIn(contest.getASportIds()).flatMap(archiveSport -> {
                    //log.info("found a sport {}", archiveSport);
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
                                                        if(place.getParallelSchoolId() == 0) {
                                                            sportSchoolDTO.setSubject(new SubjectDTO(subject2));
                                                            placeDTO.setSchool(sportSchoolDTO);
                                                            return Mono.just(placeDTO);
                                                        }else{
                                                            sportSchoolDTO.setSubject(new SubjectDTO(subject2));
                                                            placeDTO.setSchool(sportSchoolDTO);
                                                            return schoolService.getById(place.getParallelSchoolId()).flatMap(parallelSchool -> {
                                                                SportSchoolDTO parallelDTO = new SportSchoolDTO(parallelSchool);
                                                                return subjectService.getById(parallelSchool.getSubjectId()).flatMap(subject3 -> {
                                                                    parallelDTO.setSubject(new SubjectDTO(subject3));
                                                                    placeDTO.setParallelSchool(parallelDTO);
                                                                    return Mono.just(placeDTO);
                                                                });
                                                            });
                                                        }
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
                    if(sportDTOS.size() == 1){
                        if(sportDTOS.get(0).getId() == 0){
                            contestDTO.setSports(sportDTOS);
                            return getCalcSubjects(contestDTO,contest);
                        }else{
                            sportDTOS = sportDTOS.stream().sorted(Comparator.comparing(sportDTO -> sportDTO.getDiscipline().getTitle())).collect(Collectors.toList());
                            if(added != 0) {
                                sportDTOS.add(new SportDTO());
                            }
                            contestDTO.setSports(sportDTOS);
                            return getCalcSubjects(contestDTO,contest);
                        }
                    }else{
                        sportDTOS = sportDTOS.stream().sorted(Comparator.comparing(SportDTO::getId)).collect(Collectors.toList());
                        if(added != 0) {
                            sportDTOS.add(new SportDTO());
                        }
                        contestDTO.setSports(sportDTOS);
                        return getCalcSubjects(contestDTO,contest);
                    }
                });
            });
        });
    }

    private Mono<ContestDTO> getCalcSubjects(ContestDTO contestDTO, Contest contest){
        return sportService.getById(contest.getTypeOfSportId()).flatMap(sport -> baseSportService.getAllByIds(sport.getBaseSportIds()).flatMap(baseSport -> {
            int sy = baseSport.getIssueDate().getYear();
            int ey = baseSport.getExpiration();
            int cy = contest.getBeginning().getYear();
            if(sy < cy && cy < ey){
                return Mono.empty();
            }else{
                return subjectService.getById(baseSport.getSubjectId()).flatMap(subject -> {
                    int check = 0;
                    for(Integer id : subject.getBaseSportIds()){
                        if(id == baseSport.getId()){
                            check++;
                        }
                    }
                    if(check == 0){
                        return baseSportService.deleteBaseSport(baseSport.getId()).flatMap(bs -> {
                            return Mono.empty();
                        });
                    }else {
                        return Mono.just(new SubjectDTO(subject));
                    }
                });
            }
        }).collectList().flatMap(subjectsDTO -> {
            contestDTO.setBaseSubjectTotal(subjectsDTO);
            contestDTO.calcBaseIn();
            return Mono.just(contestDTO);
        }));
    }

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
