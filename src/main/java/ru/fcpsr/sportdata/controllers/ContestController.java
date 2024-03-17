package ru.fcpsr.sportdata.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.*;
import ru.fcpsr.sportdata.services.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        return contestService.getContestByEkp(contestDTO.getEkp()).flatMap(c -> {
            if(contestDTO.getBoyTotal() + contestDTO.getGirlTotal() != contestDTO.getParticipantTotal()){
                errors.rejectValue("participantTotal","","Количество М и Ж в сумме не соответствуют количество принявших участия спортсменов!");
            }
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
                    if(sport.getSportId() == 0){
                        return Mono.empty();
                    }else {
                        ArchiveSport archiveSport = new ArchiveSport(sport);
                        archiveSport.setContestId(contest.getId());
                        return archiveSportService.saveArchiveSport(archiveSport).flatMap(as -> {
                            log.info("aSport saved: " + as.toString());
                            return Flux.fromIterable(sport.getPlaces()).flatMap(placeDTO -> {
                                if (placeDTO.getParticipantId() == 0) {
                                    return Mono.empty();
                                } else {
                                    Place place = new Place(placeDTO);
                                    place.setASportId(as.getId());
                                    return placeService.addPlace(place);
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
    public Mono<Rendering> getAll(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title", "Contest information")
                        .modelAttribute("index","contest-all-page")
                        .modelAttribute("contests", getAllCompletedContest())
                        .build()
        );
    }

    private Flux<ContestDTO> getAllCompletedContest(){
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
}
