package ru.fcpsr.sportdata.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.*;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.models.Contest;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.services.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
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
    private final SportSchoolService schoolService;
    private final SubjectService subjectService;
    private final QualificationService qualificationService;

    @GetMapping("/get/all")
    public Mono<RestContest> getAllContests(@RequestParam(name = "year") int year){
        LocalDate startOfYear = LocalDate.of(year, 1, 1);
        LocalDate endOfYear = LocalDate.of(year, 12, 31);
        return getCompleteDate(startOfYear,endOfYear, -1);
    }

    @GetMapping("/get/all/by/date")
    public Mono<RestContest> getAllByDate(@RequestParam(name = "start") LocalDate start, @RequestParam(name = "end") LocalDate end, @RequestParam(name = "sport") int sportId){
        return getCompleteDate(start,end,sportId);
    }

    private Mono<RestContest> getCompleteDate(LocalDate start, LocalDate end, int sportId) {
        long currentTime = System.currentTimeMillis();
        return contestService.getAllByDate(start, end)
                .flatMap(this::buildContestDTO)
                .collectList()
                .map(contests -> filterAndSortContests(contests, sportId))
                .map(this::categorizeAndBuildRestContest)
                .map(restContest -> {
                    long delta = System.currentTimeMillis();
                    long seconds = (delta - currentTime) / 1000;
                    log.info("Выполнено за {} секунд", seconds);
                    return restContest;
                });
    }

    private Mono<ContestDTO> buildContestDTO(Contest contest) {
        ContestDTO contestDTO = new ContestDTO(contest);

        Mono<SubjectDTO> subjectMono = subjectService.getById(contest.getSubjectId()).map(SubjectDTO::new);
        Mono<TypeOfSportDTO> sportMono = sportService.findById(contest.getTypeOfSportId()).map(TypeOfSportDTO::new);
        Mono<List<SportDTO>> sportsMono = archiveSportService.getAllByIdIn(contest.getASportIds())
                .flatMap(this::buildSportDTO)
                .collectList();

        return Mono.zip(subjectMono, sportMono, sportsMono)
                .map(tuple -> {
                    contestDTO.setSubject(tuple.getT1());
                    contestDTO.setSport(tuple.getT2());
                    contestDTO.setSports(tuple.getT3());
                    return contestDTO;
                });
    }

    private Mono<SportDTO> buildSportDTO(ArchiveSport archiveSport) {
        SportDTO sportDTO = new SportDTO(archiveSport);

        Mono<DisciplineDTO> disciplineMono = disciplineService.getById(archiveSport.getDisciplineId()).map(DisciplineDTO::new);
        Mono<AgeGroupDTO> groupMono = groupService.getById(archiveSport.getAgeGroupId()).map(AgeGroupDTO::new);
        Mono<List<PlaceDTO>> placesMono = placeService.getAllByIdIn(archiveSport.getPlaceIds())
                .flatMap(this::buildPlaceDTO)
                .collectList();

        return Mono.zip(disciplineMono, groupMono, placesMono)
                .map(tuple -> {
                    sportDTO.setDiscipline(tuple.getT1());
                    sportDTO.setGroup(tuple.getT2());
                    sportDTO.setPlaces(tuple.getT3());
                    return sportDTO;
                });
    }

    private Mono<PlaceDTO> buildPlaceDTO(Place place) {
        PlaceDTO placeDTO = new PlaceDTO(place);

        Mono<QualificationDTO> qualificationMono = qualificationService.getById(place.getQualificationId()).map(QualificationDTO::new);
        Mono<ParticipantDTO> participantMono = participantService.getById(place.getParticipantId()).map(ParticipantDTO::new);
        Mono<SportSchoolDTO> schoolMono = schoolService.getById(place.getSportSchoolId())
                .flatMap(school -> {
                    SportSchoolDTO schoolDTO = new SportSchoolDTO(school);
                    return subjectService.getById(school.getSubjectId())
                            .map(SubjectDTO::new)
                            .doOnNext(schoolDTO::setSubject)
                            .thenReturn(schoolDTO);
                });

        return Mono.zip(qualificationMono, participantMono, schoolMono)
                .map(tuple -> {
                    placeDTO.setQualification(tuple.getT1());
                    placeDTO.setParticipant(tuple.getT2());
                    placeDTO.setSchool(tuple.getT3());
                    return placeDTO;
                });
    }

    private List<ContestDTO> filterAndSortContests(List<ContestDTO> contests, int sportId) {
        return contests.stream()
                .filter(contest -> sportId == -1 || contest.getSportId() == sportId)
                .sorted(Comparator.comparing(ContestDTO::getSportTitle))
                .collect(Collectors.toList());
    }

    private RestContest categorizeAndBuildRestContest(List<ContestDTO> contests) {
        RestContest restContest = new RestContest();
        restContest.setTotal(contests.size());
        restContest.setContests(contests);

        for (ContestDTO contest : contests) {
            switch (contest.getSport().getSportFilterType()) {
                case OLYMPIC -> restContest.getOlympic().add(contest);
                case NO_OLYMPIC -> restContest.getNoOlympic().add(contest);
                case ADAPTIVE -> restContest.getAdaptive().add(contest);
            }
        }

        return restContest;
    }


    /*private Mono<RestContest> getCompleteDate(LocalDate start, LocalDate end, int sportId){
        return contestService.getAllByDate(start,end).flatMap(contest -> {
            ContestDTO contestDTO = new ContestDTO(contest);
            return subjectService.getById(contest.getSubjectId()).flatMap(cs -> {
                SubjectDTO contestSubject = new SubjectDTO(cs);
                contestDTO.setSubject(contestSubject);
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
                                    return qualificationService.getById(place.getQualificationId()).flatMap(q -> {
                                        QualificationDTO qualificationDTO = new QualificationDTO(q);
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
            });
        }).flatMap(contestDTO -> {
            return contestService.getById(contestDTO.getId()).flatMap(contest -> {
                return subjectService.getByIds(contest.getTotalSubjects()).flatMap(subject -> {
                    SubjectDTO subjectDTO = new SubjectDTO(subject);
                    return Mono.just(subjectDTO);
                }).collectList().flatMap(l -> {
                    l = l.stream().sorted(Comparator.comparing(SubjectDTO::getTitle)).collect(Collectors.toList());
                    contestDTO.setSubjects(l);
                    return Mono.just(contestDTO);
                });
            });
        }).collectList().flatMap(l -> {
            List<ContestDTO> sortedList = l;
            if(sportId != -1){
                sortedList = new ArrayList<>();
                for(ContestDTO contest : l){
                    if(contest.getSportId() == sportId){
                        sortedList.add(contest);
                    }
                }
            }
            sortedList = sortedList.stream().sorted(Comparator.comparing(ContestDTO::getSportTitle)).collect(Collectors.toList());
            RestContest restContest = new RestContest();
            restContest.setTotal(sortedList.size());
            restContest.setContests(sortedList);
            for(ContestDTO contest : sortedList){
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
    }*/
}
