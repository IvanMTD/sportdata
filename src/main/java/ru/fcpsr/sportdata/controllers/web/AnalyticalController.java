package ru.fcpsr.sportdata.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.Contest;
import ru.fcpsr.sportdata.services.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticalController {

    private final ContestService contestService;
    private final ArchiveSportService archiveSportService;
    private final DisciplineService disciplineService;
    private final AgeGroupService groupService;
    private final SubjectService subjectService;
    private final BaseSportService baseSportService;
    private final TypeOfSportService sportService;
    private final ParticipantService participantService;
    private final PlaceService placeService;
    private final QualificationService qualificationService;
    private final SportSchoolService schoolService;

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
            monitoringDTO.setEkp(contest.getEkp());
            monitoringDTO.setSubjectTitle(contest.getSubjectTitle());
            monitoringDTO.setBeginning(contest.getBeginning());
            monitoringDTO.setEnding(contest.getEnding());
            monitoringDTO.setLocation(contest.getLocation());
            monitoringDTO.setContestTitle(contest.getTitle());
            monitoringDTO.setSportTitle(contest.getSportTitle());
            monitoringDTO.setCity(contest.getCity());

            monitoringDTO.setAthletesTookPart(contest.getParticipantTotal());
            monitoringDTO.setAthletesTookPartBoy(contest.getBoyTotal());
            monitoringDTO.setAthletesTookPartGirl(contest.getGirlTotal());

            monitoringDTO.setBr(contest.getBr());
            monitoringDTO.setYn1(contest.getYn1());
            monitoringDTO.setYn2(contest.getYn2());
            monitoringDTO.setYn3(contest.getYn3());
            monitoringDTO.setR1(contest.getR1());
            monitoringDTO.setR2(contest.getR2());
            monitoringDTO.setR3(contest.getR3());
            monitoringDTO.setKms(contest.getKms());
            monitoringDTO.setMs(contest.getMs());
            monitoringDTO.setMsmk(contest.getMsmk());
            monitoringDTO.setZms(contest.getZms());

            monitoringDTO.setTrainerTotal(contest.getTrainerTotal());
            monitoringDTO.setJudgeTotal(contest.getJudgeTotal());
            monitoringDTO.setNonresidentJudge(contest.getNonresidentJudge());
            monitoringDTO.setMc(contest.getMc());
            monitoringDTO.setVrc(contest.getVrc());
            monitoringDTO.setFc(contest.getFc());
            monitoringDTO.setSc(contest.getSc());
            monitoringDTO.setTc(contest.getTc());
            monitoringDTO.setBc(contest.getBc());
            monitoringDTO.setDc(contest.getDc());

            monitoringDTO.setYn1Date(contest.getYn1Date());
            monitoringDTO.setYn2Date(contest.getYn2Date());
            monitoringDTO.setYn3Date(contest.getYn3Date());
            monitoringDTO.setR1Date(contest.getR1Date());
            monitoringDTO.setR2Date(contest.getR2Date());
            monitoringDTO.setR3Date(contest.getR3Date());
            monitoringDTO.setKmsDate(contest.getKmsDate());
            monitoringDTO.setMsDate(contest.getMsDate());
            monitoringDTO.setMsmkDate(contest.getMsmkDate());
            monitoringDTO.setZmsDate(contest.getZmsDate());

            return archiveSportService.getAllByIdIn(contest.getASportIds()).flatMap(archiveSport -> {
                SportDTO sportDTO = new SportDTO(archiveSport);
                return disciplineService.getById(archiveSport.getDisciplineId()).flatMap(discipline -> {
                    sportDTO.setDiscipline(new DisciplineDTO(discipline));
                    return groupService.getById(archiveSport.getAgeGroupId()).flatMap(group -> {
                        sportDTO.setGroup(new AgeGroupDTO(group));
                        return placeService.getAllByIdIn(archiveSport.getPlaceIds()).flatMap(place -> {
                            PlaceDTO placeDTO = new PlaceDTO(place);
                            return qualificationService.getById(place.getQualificationId()).flatMap(qualification -> {
                                QualificationDTO qualificationDTO = new QualificationDTO(qualification);
                                placeDTO.setQualification(qualificationDTO);
                                return participantService.getById(place.getParticipantId()).flatMap(participant -> {
                                    ParticipantDTO participantDTO = new ParticipantDTO(participant);
                                    placeDTO.setParticipant(participantDTO);
                                    return schoolService.getById(place.getSportSchoolId()).flatMap(school -> {
                                        SportSchoolDTO schoolDTO = new SportSchoolDTO(school);
                                        return subjectService.getById(school.getSubjectId()).flatMap(subject -> {
                                            SubjectDTO subjectDTO = new SubjectDTO(subject);
                                            schoolDTO.setSubject(subjectDTO);
                                            placeDTO.setSchool(schoolDTO);
                                            return Mono.just(placeDTO);
                                        });
                                    });
                                });
                            });
                        }).collectList().flatMap(l -> {
                            l = l.stream().sorted(Comparator.comparing(PlaceDTO::getPlace)).collect(Collectors.toList());
                            sportDTO.setPlaces(l);
                            return Mono.just(sportDTO);
                        });
                    });
                });
            }).collectList().flatMap(l -> {
                l = l.stream().sorted(Comparator.comparing(SportDTO::getId)).collect(Collectors.toList());
                monitoringDTO.setDisciplines(l);
                return getSubjectMonitoring(contest).flatMap(subjectMonitoring -> {
                    monitoringDTO.setSubjectMonitoring(subjectMonitoring);
                    return Mono.just(monitoringDTO);
                });
            });
        });
    }

    private Mono<SubjectMonitoringDTO> getSubjectMonitoring(Contest contest){
        return Mono.just(new SubjectMonitoringDTO()).flatMap(subjectMonitoring -> {
            return subjectService.getByIds(contest.getTotalSubjects()).flatMap(subject -> {
                return Mono.just(new SubjectDTO(subject));
            }).collectList().flatMap(l -> {
                l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
                subjectMonitoring.setSubjectsTookPart(l);
                return Mono.just(subjectMonitoring);
            });
        }).flatMap(subjectMonitoring -> {
            return sportService.findById(contest.getTypeOfSportId()).flatMap(sport -> {
                return baseSportService.getAllByIds(sport.getBaseSportIds()).flatMap(baseSport -> {
                    int sy = baseSport.getIssueDate().getYear();
                    int ey = baseSport.getExpiration();
                    int cy = contest.getBeginning().getYear();
                    if(sy <= cy && cy < ey) {
                        return subjectService.getById(baseSport.getSubjectId()).flatMap(subject -> {
                            return Mono.just(new SubjectDTO(subject));
                        });
                    }else{
                        return Mono.empty();
                    }
                }).collectList().flatMap(l -> {
                    l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
                    subjectMonitoring.setSubjectsForSportIsBasic(l);
                    return Mono.just(subjectMonitoring);
                });
            });
        }).flatMap(subjectMonitoring -> {
            return getSubjectsBy(contest.getFirstPlace()).flatMap(fl -> {
                subjectMonitoring.setSubjectFirstPlace(fl);
                return getSubjectsBy(contest.getSecondPlace()).flatMap(sl -> {
                    subjectMonitoring.setSubjectSecondPlace(sl);
                    return getSubjectsBy(contest.getLastPlace()).flatMap(ll -> {
                        subjectMonitoring.setSubjectLastPlace(ll);
                        return Mono.just(subjectMonitoring);
                    });
                });
            });
        }).flatMap(subjectMonitoring -> {
            subjectMonitoring.setupSubjects();
            return Mono.just(subjectMonitoring);
        });
    }

    private Mono<List<SubjectDTO>> getSubjectsBy(Set<Integer> ids){
        return subjectService.getByIds(ids).flatMap(subject -> {
            return Mono.just(new SubjectDTO(subject));
        }).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
            return Mono.just(l);
        });
    }
}
