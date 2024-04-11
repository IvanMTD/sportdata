package ru.fcpsr.sportdata.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.models.Qualification;
import ru.fcpsr.sportdata.services.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/database/")
public class DataRestController {

    private final ParticipantService participantService;
    private final QualificationService qualificationService;
    private final TypeOfSportService sportService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;
    private final SportSchoolService schoolService;
    private final SubjectService subjectService;
    private final ArchiveSportService archiveSportService;
    private final PlaceService placeService;

    @GetMapping("/place/delete")
    public Mono<Place> deletePlace(@RequestParam(name = "query") String query){
        int placeId = Integer.parseInt(query);
        return placeService.deleteById(placeId).flatMap(place -> {
            log.info("'place' [{}] has been delete from db", place);
            return archiveSportService.deletePlaceFromASport(place).flatMap(archiveSport -> {
                log.info("place has been removed from archive sport [{}]",archiveSport);
                if(place.getNewQualificationId() != 0){
                    return qualificationService.deleteQualification(place.getNewQualificationId()).flatMap(qualification -> {
                        return sportService.removeQualificationFromSport(qualification).flatMap(typeOfSport -> {
                            return participantService.removeQualificationFromParticipant(qualification).flatMap(participant -> {
                                return Mono.just(place);
                            });
                        });
                    });
                }else{
                    return Mono.just(place);
                }
            });
        });
    }

    @GetMapping("/get/disciplines")
    public Flux<DisciplineDTO> getDisciplines(@RequestParam(name = "query") String query){
        int sportId = Integer.parseInt(query);
        return disciplineService.getAllBySportId(sportId).flatMap(discipline -> {
            DisciplineDTO disciplineDTO = new DisciplineDTO(discipline);
            return Mono.just(disciplineDTO);
        }).collectList().flatMapMany(dl -> {
            dl = dl.stream().sorted(Comparator.comparing(DisciplineDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(dl);
        }).flatMapSequential(Mono::just);
    }

    @GetMapping("/get/groups")
    public Flux<AgeGroupDTO> getGroups(@RequestParam(name = "query") String query){
        int sportId = Integer.parseInt(query);
        return groupService.getAllBySportId(sportId).flatMap(group -> {
            AgeGroupDTO ageGroupDTO = new AgeGroupDTO(group);
            return Mono.just(ageGroupDTO);
        }).collectList().flatMapMany(gl -> {
            gl = gl.stream().sorted(Comparator.comparing(AgeGroupDTO::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(gl);
        }).flatMapSequential(Mono::just);
    }

    @GetMapping("/get/sport")
    public Mono<TypeOfSportDTO> getSport(@RequestParam(name = "query") String query){
        int id = Integer.parseInt(query);
        return sportService.getById(id).flatMap(sport -> {
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
        });
    }
    @GetMapping("/participant")
    public Mono<ParticipantDTO> getParticipant(@RequestParam(name = "query") String query){
        return getCompletedParticipant(query);
    }

    @GetMapping("/participants")
    public Flux<Participant> getParticipants(@RequestParam(name = "query") String query){
        return participantService.findAllByLastnameLike(query);
    }

    @GetMapping("/sport/participants")
    public Flux<ParticipantDTO> getParticipantsBySportId(@RequestParam(name = "query") String query){
        int sportId = Integer.parseInt(query);
        return getCompletedParticipantBySportId(sportId);
    }

    private Flux<ParticipantDTO> getCompletedParticipantBySportId(int sportId) {
        return sportService.getById(sportId).flatMapMany(sport -> {
            log.info("FOUND SPORT: [{}]", sport);
            return qualificationService.getAllByIds(sport.getQualificationIds()).collectList().flatMap(ql -> {
                Set<Integer> participantIds = new HashSet<>();
                for(Qualification qualification : ql){
                    participantIds.add(qualification.getParticipantId());
                }
                return Mono.just(participantIds);
            }).flatMapMany(pidList -> {
                log.info("THIS IS PID LIST: [{}]", pidList);
                return participantService.getAllByIdIn(pidList).flatMap(participant -> {
                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                    return qualificationService.getAllByIds(participant.getQualificationIds()).flatMap(qualification -> {
                        QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                        return sportService.findById(qualification.getTypeOfSportId()).flatMap(qSport -> {
                            qualificationDTO.setSport(new TypeOfSportDTO(qSport));
                            return Mono.just(qualificationDTO);
                        });
                    }).collectList().flatMap(ql -> {
                        log.info("THIS IS QUALIFICATION LIST: [{}]", ql);
                        ql = ql.stream().sorted(Comparator.comparing(qualification -> qualification.getCategory().getTitle())).collect(Collectors.toList());
                        participantDTO.setQualifications(ql);
                        return schoolService.getAllByIdIn(participant.getSportSchoolIds()).flatMap(school -> {
                            SportSchoolDTO sportSchoolDTO = new SportSchoolDTO(school);
                            return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                                sportSchoolDTO.setSubject(new SubjectDTO(subject));
                                return Mono.just(sportSchoolDTO);
                            });
                        }).collectList().flatMap(sl -> {
                            log.info("THIS IS SCHOOL LIST: [{}]", sl);
                            sl = sl.stream().sorted(Comparator.comparing(SportSchoolDTO::getTitle)).collect(Collectors.toList());
                            participantDTO.setSchools(sl);
                            return Mono.just(participantDTO);
                        });
                    });
                });
            }).collectList().flatMapMany(pl -> {
                log.info("THIS IS PARTICIPANT LIST: [{}]", pl);
                pl = pl.stream().sorted(Comparator.comparing(ParticipantDTO::getFullName)).collect(Collectors.toList());
                return Flux.fromIterable(pl);
            }).flatMapSequential(Mono::just);
        });
        /*return qualificationService.getAllBySportId(sportId).flatMap(qualification -> {
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
        }).flatMapSequential(Mono::just);*/
    }

    private Mono<ParticipantDTO> getCompletedParticipant(String fullName) {
        return participantService.findByFullName(fullName).flatMap(participant -> {
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
}
