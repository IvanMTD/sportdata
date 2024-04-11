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
import ru.fcpsr.sportdata.validators.BaseSportValidation;
import ru.fcpsr.sportdata.validators.ParticipantValidation;
import ru.fcpsr.sportdata.validators.SchoolValidation;
import ru.fcpsr.sportdata.validators.SubjectValidation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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

    private final QualificationService qualificationService;

    private final SportSchoolService schoolService;

    private final BaseSportService baseSportService;

    private final BaseSportValidation baseSportValidation;

    private final SchoolValidation schoolValidation;

    private final SubjectValidation subjectValidation;

    private final ParticipantValidation participantValidation;

    @GetMapping("/summary")
    public Mono<Rendering> getSummary(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Summary information")
                        .modelAttribute("index","data-sum-page")
                        .modelAttribute("dbStat", getTotalStatistic())
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
                        .modelAttribute("sports", getCompletedTypeOfSport(letter))
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
            return getBaseSportErrorRendering(letter,sportDTO);
        }).switchIfEmpty(Mono.just(sportDTO).flatMap(sport -> {
            if(errors.hasErrors()){
                return getBaseSportErrorRendering(letter,sportDTO);
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
            return getBaseSportErrorRendering(letter,sportDTO);
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

    @GetMapping("/{letter}/sport/{sportId}/discipline/{disciplineId}/delete")
    public Mono<Rendering> deleteDisciplineFromSport(@PathVariable String letter, @PathVariable int sportId, @PathVariable int disciplineId){
        return disciplineService.deleteDiscipline(disciplineId).flatMap(discipline -> {
            log.info("discipline has been deleted: " + discipline.toString());
            return sportService.deleteDisciplineFromSport(discipline).flatMap(sport -> {
                log.info("discipline has been removed from sport " + sport.getDisciplineIds());
                return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
            });
        });
    }

    @PostMapping("/sport/{letter}/add/group")
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
            log.info("group saved " + group.toString());
            return sportService.addGroupInSport(group).flatMap(sport -> {
                log.info("group added in sport " + sport.getAgeGroupIds());
                return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
            });
        });
    }

    @GetMapping("/{letter}/sport/{sportId}/group/{groupId}/delete")
    public Mono<Rendering> deleteGroupFromSport(@PathVariable String letter, @PathVariable int sportId, @PathVariable int groupId){
        return groupService.deleteGroup(groupId).flatMap(group -> {
            log.info("group has been deleted " + group.toString());
            return sportService.deleteGroupFromSport(group).flatMap(sport -> {
                log.info("group has been removed from sport " + sport.getAgeGroupIds());
                return Mono.just(Rendering.redirectTo("/database/sport/" + letter).build());
            });
        });
    }

    /**
     * УПРАВЛЕНИЕ БАЗОЙ ДАННЫХ - СУБЪЕКТЫ
     * @param letter
     * @return
     */

    @GetMapping("/subject/{letter}")
    public Mono<Rendering> showSubjects(@PathVariable(name = "letter") String letter){
        Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);

        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Subject data")
                        .modelAttribute("index","subject-data-page")
                        .modelAttribute("subjects", getCompletedSubjects(letter))
                        .modelAttribute("subjectForm", new SubjectDTO())
                        .modelAttribute("baseSportForm", new BaseSportDTO())
                        .modelAttribute("schoolForm", new SportSchoolDTO())
                        .modelAttribute("participantForm", new ParticipantDTO())
                        .modelAttribute("federals", FederalDistrict.getDistricts())
                        .modelAttribute("sportList", sports)
                        .modelAttribute("letter",letter)
                        .build()
        );
    }

    @PostMapping("/subject/{letter}/add")
    public Mono<Rendering> addNewSubject(@PathVariable String letter, @ModelAttribute(name = "subjectForm") @Valid SubjectDTO subjectDTO, Errors errors){
        return subjectService.findByTitle(subjectDTO.getTitle()).flatMap(subject -> {
            if(subject.getId() != 0){
                errors.rejectValue("title","","Такой субъект уже есть!");
            }
            if(errors.hasErrors()){
                Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
                    l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
                    return Flux.fromIterable(l);
                }).flatMapSequential(Mono::just);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Subject data")
                                .modelAttribute("index","subject-data-page")
                                .modelAttribute("subjects", getCompletedSubjects(letter))
                                .modelAttribute("subjectForm", subjectDTO)
                                .modelAttribute("baseSportForm", new BaseSportDTO())
                                .modelAttribute("schoolForm", new SportSchoolDTO())
                                .modelAttribute("participantForm", new ParticipantDTO())
                                .modelAttribute("federals", FederalDistrict.getDistricts())
                                .modelAttribute("sportList", sports)
                                .modelAttribute("letter",letter)
                                .build()
                );
            }

            return subjectService.save(subjectDTO).flatMap(s -> {
                log.info("subject saved: " + s.toString());
                return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
            });
        });
    }

    @PostMapping("/subject/{letter}/add/base/sport")
    public Mono<Rendering> addBaseSportInSubject(@PathVariable String letter, @ModelAttribute(name = "baseSportForm") @Valid BaseSportDTO baseSportDTO, Errors errors){
        baseSportValidation.validate(baseSportDTO,errors);
        if (errors.hasErrors()){
            Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
                l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
                return Flux.fromIterable(l);
            }).flatMapSequential(Mono::just);
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Subject data")
                            .modelAttribute("index","subject-data-page")
                            .modelAttribute("subjects", getCompletedSubjects(letter))
                            .modelAttribute("subjectForm", new SubjectDTO())
                            .modelAttribute("baseSportForm", baseSportDTO)
                            .modelAttribute("schoolForm", new SportSchoolDTO())
                            .modelAttribute("participantForm", new ParticipantDTO())
                            .modelAttribute("federals", FederalDistrict.getDistricts())
                            .modelAttribute("sportList", sports)
                            .modelAttribute("letter",letter)
                            .build()
            );
        }

        return baseSportService.addNewBaseSport(baseSportDTO).flatMap(baseSport -> {
            log.info("base sport created " + baseSport.getId());
            return sportService.addBaseSportInSport(baseSport).flatMap(sport -> {
                log.info("baseSport added in to sport: " + sport.getBaseSportIds());
                return subjectService.addBaseSportInSubject(baseSport).flatMap(subject -> {
                    log.info("baseSport added in to subject: " + subject.getBaseSportIds());
                    return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
                });
            });
        });
    }

    @GetMapping("/{letter}/delete/base/sport/{bSportId}")
    public Mono<Rendering> deleteBaseSportFromSubject(@PathVariable String letter, @PathVariable int bSportId){
        return baseSportService.deleteBaseSport(bSportId).flatMap(baseSport -> {
            log.info("baseSport deleted: " + baseSport.getId());
            return sportService.deleteBaseSportFromSport(baseSport).flatMap(sport -> {
                log.info("baseSport has been removed from sport: " + sport.getBaseSportIds());
                return subjectService.removeBaseSportFromSubject(baseSport).flatMap(subject -> {
                    log.info("baseSport has been removed from subject: " + subject.getBaseSportIds());
                    return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
                });
            });
        });
    }

    @PostMapping("/subject/{letter}/add/school")
    public Mono<Rendering> addSchoolInSubject(@PathVariable(name = "letter") String letter, @ModelAttribute(name = "schoolForm") @Valid SportSchoolDTO sportSchoolDTO, Errors errors){
        return schoolService.findByTitleAndSubjectId(sportSchoolDTO).flatMap(school -> {
            schoolValidation.setSportSchool(school);
            schoolValidation.validate(sportSchoolDTO,errors);
            if(errors.hasErrors()){
                Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
                    l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
                    return Flux.fromIterable(l);
                }).flatMapSequential(Mono::just);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Subject data")
                                .modelAttribute("index","subject-data-page")
                                .modelAttribute("subjects", getCompletedSubjects(letter))
                                .modelAttribute("subjectForm", new SubjectDTO())
                                .modelAttribute("baseSportForm", new BaseSportDTO())
                                .modelAttribute("schoolForm", sportSchoolDTO)
                                .modelAttribute("participantForm", new ParticipantDTO())
                                .modelAttribute("federals", FederalDistrict.getDistricts())
                                .modelAttribute("sportList", sports)
                                .modelAttribute("letter",letter)
                                .build()
                );
            }

            return schoolService.saveSchool(sportSchoolDTO).flatMap(sportSchool -> {
                log.info("school saved: " + sportSchool.getId());
                return subjectService.addSchoolInSubject(sportSchool).flatMap(subject -> {
                    log.info("school added in subject: " + subject.getSportSchoolIds());
                    return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
                });
            });
        });
    }

    @PostMapping("/subject/{letter}/title/update")
    public Mono<Rendering> subjectTitleUpdate(@PathVariable String letter, @ModelAttribute(name = "subjectForm") @Valid SubjectDTO subjectDTO, Errors errors){
        return subjectService.findByTitle(subjectDTO.getTitle()).flatMap(subject -> {
            subjectValidation.setSubject(subject);
            subjectValidation.validate(subjectDTO,errors);
            if(errors.hasFieldErrors("title")){
                Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
                    l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
                    return Flux.fromIterable(l);
                }).flatMapSequential(Mono::just);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Subject data")
                                .modelAttribute("index","subject-data-page")
                                .modelAttribute("subjects", getCompletedSubjects(letter))
                                .modelAttribute("subjectForm", subjectDTO)
                                .modelAttribute("baseSportForm", new BaseSportDTO())
                                .modelAttribute("schoolForm", new SportSchoolDTO())
                                .modelAttribute("participantForm", new ParticipantDTO())
                                .modelAttribute("federals", FederalDistrict.getDistricts())
                                .modelAttribute("sportList", sports)
                                .modelAttribute("letter",letter)
                                .build()
                );
            }

            return subjectService.updateTitle(subjectDTO).flatMap(s -> {
                log.info("title in subject has been updated: " + s.getTitle());
                return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
            });
        });
    }

    @PostMapping("/subject/{letter}/iso/update")
    public Mono<Rendering> updateIsoInSubject(@PathVariable String letter, @ModelAttribute(name = "subjectForm") @Valid SubjectDTO subjectDTO, Errors errors){
        return subjectService.findByISO(subjectDTO.getIso()).flatMap(subject -> {
            subjectValidation.setSubject(subject);
            subjectValidation.validate(subjectDTO,errors);
            if(errors.hasFieldErrors("iso")){
                Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
                    l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
                    return Flux.fromIterable(l);
                }).flatMapSequential(Mono::just);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Subject data")
                                .modelAttribute("index","subject-data-page")
                                .modelAttribute("subjects", getCompletedSubjects(letter))
                                .modelAttribute("subjectForm", subjectDTO)
                                .modelAttribute("baseSportForm", new BaseSportDTO())
                                .modelAttribute("schoolForm", new SportSchoolDTO())
                                .modelAttribute("participantForm", new ParticipantDTO())
                                .modelAttribute("federals", FederalDistrict.getDistricts())
                                .modelAttribute("sportList", sports)
                                .modelAttribute("letter",letter)
                                .build()
                );
            }

            return subjectService.updateISO(subjectDTO).flatMap(s -> {
                log.info("ISO in subject has been updated: " + s.getIso());
                return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
            });
        });
    }

    @PostMapping("/subject/{letter}/school/update")
    public Mono<Rendering> updateSchool(@PathVariable String letter, @ModelAttribute(name = "schoolForm") @Valid SportSchoolDTO sportSchoolDTO, Errors errors){
        return schoolService.findByTitleAndSubjectId(sportSchoolDTO).flatMap(sportSchool -> {
            schoolValidation.setSportSchool(sportSchool);
            schoolValidation.validate(sportSchoolDTO, errors);
            if(errors.hasErrors()){
                Flux<TypeOfSport> sports = sportService.getAll().collectList().flatMapMany(l -> {
                    l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
                    return Flux.fromIterable(l);
                }).flatMapSequential(Mono::just);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Subject data")
                                .modelAttribute("index","subject-data-page")
                                .modelAttribute("subjects", getCompletedSubjects(letter))
                                .modelAttribute("subjectForm", new SubjectDTO())
                                .modelAttribute("baseSportForm", new BaseSportDTO())
                                .modelAttribute("schoolForm", sportSchoolDTO)
                                .modelAttribute("participantForm", new ParticipantDTO())
                                .modelAttribute("federals", FederalDistrict.getDistricts())
                                .modelAttribute("sportList", sports)
                                .modelAttribute("letter",letter)
                                .build()
                );
            }

            return schoolService.updateTitleAndAddress(sportSchoolDTO).flatMap(school -> {
                log.info("title and address in school updated " + school.toString());
                return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
            });
        });
    }

    @GetMapping("/{schoolId}/subject/{letter}/school/participant/add")
    public Mono<Rendering> addParticipantInSchool(@PathVariable int schoolId, @PathVariable String letter, @RequestParam(name = "search") String search){
        return participantService.findByFullName(search).flatMap(participant -> {
            participant.addSportSchoolId(schoolId);
            if(participant.getId() == 0){
                return Mono.just(participant);
            }else {
                return participantService.saveParticipant(participant);
            }
        }).flatMap(participant -> {
            if(participant.getId() == 0){
                return Mono.just(Rendering.redirectTo("/database/participant/" + participant.getId() + "/show").build());
            }else {
                log.info("school info added in participant: " + participant.getSportSchoolIds());
                return schoolService.addParticipantInSchool(schoolId, participant).flatMap(school -> {
                    log.info("participant added in school " + school.getParticipantIds());
                    return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
                });
            }
        });
    }

    @GetMapping("/subject/{letter}/school/{sid}/remove/participant/{pid}")
    public Mono<Rendering> removeParticipantFromSchool(@PathVariable String letter, @PathVariable int sid, @PathVariable int pid){
        return schoolService.removeParticipantFromSchool(sid,pid).flatMap(sportSchool -> {
            log.info("participant removed from school: " + sportSchool.getParticipantIds());
            return participantService.removeSchoolFromParticipant(pid,sid).flatMap(participant -> {
                log.info("school removed from participant: " + participant.getSportSchoolIds());
                return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
            });
        });
    }

    @GetMapping("/{letter}/subject/{subjectId}/school/{schoolId}/delete")
    public Mono<Rendering> deleteSchool(@PathVariable String letter, @PathVariable int subjectId, @PathVariable int schoolId){
        return schoolService.deleteSchool(schoolId).flatMap(sportSchool -> {
            log.info("school deleted: " + sportSchool.toString());
            return participantService.removeSchoolFromParticipants(sportSchool).collectList().flatMap(participants -> {
                for(Participant participant : participants){
                    log.info("school has been removed from participant " + participant.getSportSchoolIds());
                }
                return subjectService.removeSchoolFromSubject(subjectId,schoolId).flatMap(subject -> {
                    log.info("school has been removed from subject " + subject.getSportSchoolIds());
                    return Mono.just(Rendering.redirectTo("/database/subject/" + letter).build());
                });
            });
        });
    }

    @GetMapping("/subject/{letter}/delete/{subjectId}")
    public Mono<Rendering> deleteSubject(@PathVariable String letter, @PathVariable int subjectId){
        /*
        1. Удаляем субъект +
        2. Удаляем карточку базового спорта +
        3. Удаляем карточку базового спорта из спорта +
        4. Удаляем школу
        5. Удаляем школу из спортсмена
         */
        return subjectService.deleteSubject(subjectId).flatMap(subject -> {
            log.info("subject has been deleted: " + subject.toString());
            log.info("step 1");
            return baseSportService.deleteAllBaseSports(subject.getBaseSportIds()).flatMap(baseSport -> {
                log.info("baseSport has been deleted " + baseSport.toString());
                return sportService.deleteBaseSportFromSport(baseSport).flatMap(sport -> {
                    log.info("baseSport has been removed from sport " + sport.getBaseSportIds());
                    return Mono.just(sport);
                });
            }).collectList().flatMap(sportList -> {
                log.info("step 2");
                return schoolService.deleteSchoolsByIds(subject.getSportSchoolIds()).flatMap(school -> {
                    log.info("school has been deleted " + school.toString());
                    return participantService.removeSchoolFromParticipants(school).flatMap(participant -> {
                        log.info("school has been removed from participant " + participant.getSportSchoolIds());
                        return Mono.just(participant);
                    });
                }).collectList().flatMap(l -> Mono.just(Rendering.redirectTo("/database/subject/" + letter).build()));
            });
        });
    }

    /**
     * УПРАВЛЕНИЕ БАЗОЙ ДАННОЙ - УЧАСТНИКИ
     * @return
     */

    @GetMapping("/participant/{letter}")
    public Mono<Rendering> showParticipant(@PathVariable String letter){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Participant page")
                        .modelAttribute("index", "participant-data-page")
                        .modelAttribute("participants", getCompletedParticipantByFirstLetter(letter))
                        .modelAttribute("participantForm", new ParticipantDTO())
                        .modelAttribute("letter",letter)
                        .build()
        );
    }

    @PostMapping("/participant/{letter}/add")
    public Mono<Rendering> addNewParticipant(@PathVariable String letter, @ModelAttribute(name = "participantForm") @Valid ParticipantDTO participantDTO, Errors errors){
        return participantService.findByFullNameAndBirthday(participantDTO).flatMap(participant -> {
            participantValidation.setParticipant(participant);
            participantValidation.validate(participantDTO, errors);
            if(errors.hasErrors()){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Participant page")
                                .modelAttribute("index", "participant-data-page")
                                .modelAttribute("participants", getCompletedParticipantByFirstLetter(letter))
                                .modelAttribute("participantForm", participantDTO)
                                .modelAttribute("letter",letter)
                                .build()
                );
            }

            return participantService.addNewParticipant(participantDTO).flatMap(p -> {
                log.info("participant saved " + participant.toString());
                return Mono.just(Rendering.redirectTo("/database/participant/" + letter).build());
            });
        });
    }

    @GetMapping("/participant/search")
    public Mono<Rendering> searchParticipant(@RequestParam(name = "search") String search){
        return participantService.findByFullName(search).flatMap(participant -> Mono.just(Rendering.redirectTo("/database/participant/" + participant.getId() + "/show").build()));
    }

    @GetMapping("/participant/{id}/show")
    public Mono<Rendering> showParticipant(@PathVariable int id){
        if(id == 0){
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Participant page")
                            .modelAttribute("index","participant-page")
                            .modelAttribute("participant", new Participant())
                            .modelAttribute("participantForm", new ParticipantDTO())
                            .modelAttribute("subjects", getCompletedSubjects())
                            .modelAttribute("qualificationForm", new QualificationDTO())
                            .modelAttribute("sports", sportService.getAll())
                            .modelAttribute("category", Category.values())
                            .modelAttribute("dataEdit",false)
                            .build()
            );
        }else{
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Participant page")
                            .modelAttribute("index","participant-page")
                            .modelAttribute("participant", getCompletedParticipant(id))
                            .modelAttribute("participantForm", new ParticipantDTO())
                            .modelAttribute("subjects", getCompletedSubjects())
                            .modelAttribute("qualificationForm", new QualificationDTO())
                            .modelAttribute("sports", sportService.getAll())
                            .modelAttribute("category", Category.values())
                            .modelAttribute("dataEdit",false)
                            .build()
            );
        }
    }

    @PostMapping("/participant/data/update")
    public Mono<Rendering> participantDataUpdate(@ModelAttribute(name = "participantForm") @Valid ParticipantDTO participantDTO, Errors errors){
        return participantService.getById(participantDTO.getId()).flatMap(participant -> {
            participantValidation.setParticipant(participant);
            participantValidation.validate(participantDTO,errors);
            if(errors.hasErrors()){
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Participant page")
                                .modelAttribute("index","participant-page")
                                .modelAttribute("participant", getCompletedParticipant(participantDTO.getId()))
                                .modelAttribute("participantForm", participantDTO)
                                .modelAttribute("subjects", getCompletedSubjects())
                                .modelAttribute("qualificationForm", new QualificationDTO())
                                .modelAttribute("sports", sportService.getAll())
                                .modelAttribute("category", Category.values())
                                .modelAttribute("dataEdit",true)
                                .build()
                );
            }

            return participantService.updateParticipantData(participantDTO).flatMap(part -> {
                log.info("participant updated " + part.toString());
                return Mono.just(Rendering.redirectTo("/database/participant/" + part.getId() + "/show").build());
            });
        });
    }

    @PostMapping("/participant/subject/school/update")
    public Mono<Rendering> participantUpdateSubjectAndSchool(@ModelAttribute(name = "participant") ParticipantDTO participantDTO){
        return participantService.updateSchool(participantDTO.getId(), participantDTO.getOldSchoolId(), participantDTO.getSchoolId()).flatMap(participant -> {
            log.info("school in participant updated: " + participant.getSportSchoolIds());
            return schoolService.updateSchoolParticipant(participantDTO.getId(), participantDTO.getOldSchoolId(), participantDTO.getSchoolId()).flatMap(school -> {
                log.info("participant added in new school " + school.getParticipantIds());
                return Mono.just(Rendering.redirectTo("/database/participant/" + participantDTO.getId() + "/show").build());
            });
        });
    }

    @PostMapping("/participant/subject/school/add")
    public Mono<Rendering> participantAddNewSchool(@ModelAttribute(name = "participant") ParticipantDTO participantDTO){
        return participantService.updateSchool(participantDTO.getId(), participantDTO.getSchoolId()).flatMap(participant -> {
            log.info("school added in participant list " + participant.getSportSchoolIds());
            return schoolService.addParticipantInSchool(participantDTO.getSchoolId(),participant).flatMap(school -> {
                log.info("participant added in new school " + school.getParticipantIds());
                return Mono.just(Rendering.redirectTo("/database/participant/" + participantDTO.getId() + "/show").build());
            });
        });
    }

    @GetMapping("/participant/{pid}/subject/school/{schoolId}/delete")
    public Mono<Rendering> deleteSchoolFromParticipant(@PathVariable int pid, @PathVariable int schoolId){
        return participantService.removeSchoolFromParticipant(pid,schoolId).flatMap(participant -> {
            log.info("school has been removed from participant " + participant.getSportSchoolIds());
            return schoolService.removeParticipantFromSchool(schoolId,pid).flatMap(school -> {
                log.info("partcipant has been removed from school " + school.getParticipantIds());
                return Mono.just(Rendering.redirectTo("/database/participant/" + pid + "/show").build());
            });
        });
    }

    @PostMapping("/participant/{pid}/qualification/update")
    public Mono<Rendering> updateQualificationParticipant(@PathVariable int pid, @ModelAttribute(name = "qualificationForm") QualificationDTO qualificationDTO){
        return qualificationService.getById(qualificationDTO.getId()).flatMap(qualification -> {
            log.info("took original qualification " + qualification.toString());
            return qualificationService.updateQualification(qualificationDTO).flatMap(updatedQualification -> {
                log.info("qualification updated " + updatedQualification.toString());
                return sportService.removeQualificationFromSport(qualification).flatMap(sport -> {
                    log.info("old qualification has been removed from sport " + sport.getQualificationIds());
                    return sportService.addQualificationInSport(qualificationDTO).flatMap(updatedSport -> {
                        log.info("new qualification added in sport " + updatedSport.getQualificationIds());
                        return Mono.just(Rendering.redirectTo("/database/participant/" + pid + "/show").build());
                    });
                });
            });
        });
    }

    @GetMapping("/participant/{pid}/qualification/{qid}/delete")
    public Mono<Rendering> deleteQualificationFromParticipant(@PathVariable int pid, @PathVariable int qid){
        return qualificationService.deleteQualification(qid).flatMap(qualification -> {
            log.info("qualification has been deleted " + qualification.toString());
            return sportService.removeQualificationFromSport(qualification).flatMap(sport -> {
                log.info("qualification has been removed from sport " + sport.getQualificationIds());
                return participantService.removeQualificationFromParticipant(qualification).flatMap(participant -> {
                    log.info("qualification has been removed from participant " + participant.getQualificationIds());
                    return Mono.just(Rendering.redirectTo("/database/participant/" + pid + "/show").build());
                });
            });
        });
    }

    @PostMapping("/participant/qualification/add")
    public Mono<Rendering> addQualification(@ModelAttribute(name = "qualificationForm") QualificationDTO qualificationDTO){
        return qualificationService.createNewQualification(qualificationDTO).flatMap(qualification -> {
            log.info("created new qualification " + qualification.toString());
            return participantService.addQualificationToParticipant(qualification).flatMap(participant -> {
                log.info("qualification added to participant " + participant.getQualificationIds());
                return sportService.addQualificationInSport(qualification).flatMap(sport -> {
                    log.info("qualification added in sport " + sport.getQualificationIds());
                    return Mono.just(Rendering.redirectTo("/database/participant/" + qualificationDTO.getParticipantId() + "/show").build());
                });
            });
        });
    }

    @GetMapping("/participant/{pid}/delete")
    public Mono<Rendering> deleteParticipant(@PathVariable int pid){
        return participantService.deleteParticipant(pid).flatMap(participant -> {
            log.info("participant has been deleted! - " + participant.toString());
            log.info("STEP 1");
            /*
            * 1. Удалить все Квалификации и записи о них в Спорте
            * 2. Удалить все записи об участнике в школах
            * */
            return qualificationService.deleteQualifications(participant.getQualificationIds()).flatMap(qualification -> {
                log.info("qualification has been deleted! - " + qualification.toString());
                return sportService.removeQualificationFromSport(qualification).flatMap(sport -> {
                    log.info("qualification has been removed from sport " + sport.getQualificationIds());
                    return Mono.just(qualification);
                });
            }).collectList().flatMap(ql -> {
                log.info("STEP 2");
                return schoolService.removeParticipantFromSchools(participant).flatMap(school -> {
                    log.info("participant has been removed from school " + school.getParticipantIds());
                    return Mono.just(school);
                }).collectList().flatMap(sl -> {
                    log.info("delete completed");
                    return Mono.just(Rendering.redirectTo("/database/participant/А").build());
                });
            });
        });
    }

    /*@GetMapping("/participant/search")
    public Mono<Rendering> searchParticipant(@RequestParam(name = "search") String fullName){
        Flux<Participant> participantFlux = participantService.getAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
        Flux<Subject> subjectFlux = subjectService.getAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Subject::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Search result")
                        .modelAttribute("index", "participant-page")
                        .modelAttribute("pList", participantFlux)
                        .modelAttribute("participants", getCompletedParticipant(fullName))
                        .modelAttribute("subjectList", subjectFlux)
                        .modelAttribute("sspForm", new SubSportPartDTO())
                        .build()
        );
    }*/

    /**
     * ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
     * @param
     * @return
     */

    private Mono<ParticipantDTO> getCompletedParticipant(int id) {
        return participantService.getById(id).flatMap(participant -> {
            ParticipantDTO participantDTO = new ParticipantDTO(participant);
            return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                return sportService.getById(qualification.getTypeOfSportId()).flatMap(sport -> {
                    TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                    qualificationDTO.setSport(typeOfSportDTO);
                    return Mono.just(qualificationDTO);
                });
            }).collectList().flatMap(ql -> {
                ql = ql.stream().sorted(Comparator.comparing(QualificationDTO::getCategory)).collect(Collectors.toList());
                participantDTO.setQualifications(ql);
                return schoolService.getAllByIdIn(participant.getSportSchoolIds()).flatMap(school -> {
                    SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                    return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        sportSchoolDTO.setSubject(subjectDTO);
                        return Mono.just(sportSchoolDTO);
                    });
                }).collectList().flatMap(schools -> {
                    schools = schools.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                    participantDTO.setSchools(schools);
                    return Mono.just(participantDTO);
                });
            });
        });
    }

    private Flux<ParticipantDTO> getCompletedParticipantByFirstLetter(String letter){
        /*
        1. Получаем спортсменов
        2. Собираем квалификацию
        3. Собираем виды спорта по квалификации
        4. Собираем школы
        5. Собираем субъекты
         */
        return participantService.getAllByFirstLetter(letter).flatMap(participant -> {
            ParticipantDTO participantDTO = new ParticipantDTO(participant);
            return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                return sportService.getById(qualification.getTypeOfSportId()).flatMap(sport -> {
                    TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                    qualificationDTO.setSport(typeOfSportDTO);
                    return Mono.just(qualificationDTO);
                });
            }).collectList().flatMap(ql -> {
                ql = ql.stream().sorted(Comparator.comparing(QualificationDTO::getCategory)).collect(Collectors.toList());
                participantDTO.setQualifications(ql);
                return schoolService.getAllByIdIn(participant.getSportSchoolIds()).flatMap(school -> {
                    SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                    return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        sportSchoolDTO.setSubject(subjectDTO);
                        return Mono.just(sportSchoolDTO);
                    });
                }).collectList().flatMap(schools -> {
                    schools = schools.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                    participantDTO.setSchools(schools);
                    return Mono.just(participantDTO);
                });
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(ParticipantDTO::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    private Flux<SubjectDTO> getCompletedSubjects() {
        return subjectService.getAll().flatMap(subject -> {
            SubjectDTO subjectDTO = new SubjectDTO(subject);
            return schoolService.getAllByIdIn(subject.getSportSchoolIds()).flatMap(school -> {
                SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                return Mono.just(sportSchoolDTO);
            }).collectList().flatMap(schools -> {
                subjectDTO.setSchools(schools);
                return Mono.just(subjectDTO);
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    private Flux<SubjectDTO> getCompletedSubjects(String letter){
        return subjectService.getSubjectsByFirstLetter(letter).flatMap(subject -> {
            SubjectDTO subjectDTO = new SubjectDTO(subject);
            return baseSportService.getAllByIds(subject.getBaseSportIds()).flatMap(baseSport -> {
                BaseSportDTO baseSportDTO = new BaseSportDTO(baseSport);
                return sportService.getById(baseSport.getTypeOfSportId()).flatMap(sport -> {
                    TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                    baseSportDTO.setSport(typeOfSportDTO);
                    return Mono.just(baseSportDTO);
                });
            }).collectList().flatMap(baseSportDTOS -> {
                baseSportDTOS = baseSportDTOS.stream().sorted(Comparator.comparing(baseSportDTO -> baseSportDTO.getSport().getTitle())).collect(Collectors.toList());
                subjectDTO.setBaseSports(baseSportDTOS);
                return schoolService.getAllByIdIn(subject.getSportSchoolIds()).flatMap(school -> {
                    SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                    return participantService.findByIds(school.getParticipantIds()).flatMap(participant -> {
                        ParticipantDTO participantDTO = new ParticipantDTO(participant);
                        return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                            QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                            return sportService.getById(qualification.getTypeOfSportId()).flatMap(sport -> {
                                TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
                                qualificationDTO.setSport(typeOfSportDTO);
                                return Mono.just(qualificationDTO);
                            });
                        }).collectList().flatMap(ql -> {
                            participantDTO.setQualifications(ql);
                            return Mono.just(participantDTO);
                        });
                    }).collectList().flatMap(participantDTOS -> {
                        sportSchoolDTO.setParticipants(participantDTOS);
                        return Mono.just(sportSchoolDTO);
                    });
                }).collectList().flatMap(sportSchoolDTOS -> {
                    sportSchoolDTOS = sportSchoolDTOS.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                    subjectDTO.setSchools(sportSchoolDTOS);
                    return Mono.just(subjectDTO);
                });
            });
        }).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    private Flux<TypeOfSportDTO> getCompletedTypeOfSport(String letter){
        return sportService.getSportsByFirstLetter(letter).flatMap(sport -> {
            TypeOfSportDTO typeOfSportDTO = new TypeOfSportDTO(sport);
            return baseSportService.getAllByIds(sport.getBaseSportIds()).flatMap(baseSport -> {
                if(LocalDate.now().getYear() < baseSport.getExpiration()) {
                    BaseSportDTO baseSportDTO = new BaseSportDTO(baseSport);
                    return subjectService.getById(baseSport.getSubjectId()).flatMap(subject -> {
                        SubjectDTO subjectDTO = new SubjectDTO(subject);
                        baseSportDTO.setSubject(subjectDTO);
                        return Mono.just(baseSportDTO);
                    });
                }else{
                    return Mono.empty();
                }
            }).collectList().flatMap(bsl -> {
                bsl = bsl.stream().sorted(Comparator.comparing(baseSportDTO -> baseSportDTO.getSubject().getTitle())).collect(Collectors.toList());
                typeOfSportDTO.setBaseSports(bsl);
                return disciplineService.getAllByIds(sport.getDisciplineIds()).collectList().flatMap(disciplines -> {
                    List<DisciplineDTO> dl = new ArrayList<>();
                    for(Discipline discipline : disciplines){
                        dl.add(new DisciplineDTO(discipline));
                    }
                    dl = dl.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
                    typeOfSportDTO.setDisciplines(dl);
                    return groupService.getAllByIds(sport.getAgeGroupIds()).collectList();
                }).flatMap(groups -> {
                    List<AgeGroupDTO> ag = new ArrayList<>();
                    for(AgeGroup ageGroup : groups){
                        ag.add(new AgeGroupDTO(ageGroup));
                    }
                    ag = ag.stream().sorted(Comparator.comparing(AgeGroupDTO::getTitle)).collect(Collectors.toList());
                    typeOfSportDTO.setGroups(ag);
                    return Mono.just(typeOfSportDTO);
                });
            });
        }).collectList().flatMapMany(sports -> {
            sports = sports.stream().sorted(Comparator.comparing(TypeOfSportDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(sports);
        }).flatMapSequential(Mono::just);
    }

    private Mono<TotalStatisticDTO> getTotalStatistic(){
        return Mono.just(new TotalStatisticDTO()).flatMap(form -> sportService.getCount().flatMap(count -> {
            form.setSportTotal(Math.toIntExact(count));
            return disciplineService.getCount();
        }).flatMap(count -> {
            form.setDisciplineTotal(Math.toIntExact(count));
            return groupService.getCount();
        }).flatMap(count -> {
            form.setGroupTotal(Math.toIntExact(count));
            return subjectService.getCount();
        }).flatMap(count -> {
            form.setSubjectTotal(Math.toIntExact(count));
            return schoolService.getCount();
        }).flatMap(count -> {
            form.setSchoolTotal(Math.toIntExact(count));
            return participantService.getCount();
        }).flatMap(count -> {
            form.setParticipantTotal(Math.toIntExact(count));
            return qualificationService.getCount();
        }).flatMap(count -> {
            form.setQualificationTotal(Math.toIntExact(count));
            return Mono.just(form);
        }));
    }

    private Mono<Rendering> getBaseSportErrorRendering(String letter, TypeOfSportDTO sportDTO){
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
}
