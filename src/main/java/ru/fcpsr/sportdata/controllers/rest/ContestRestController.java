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
import ru.fcpsr.sportdata.enums.SportFilterType;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.models.Contest;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.services.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        long startTime = System.currentTimeMillis();

        return contestService.getAllByDate(start, end)
                .flatMap(this::processContest)
                .collectList()
                .flatMap(contests -> processContestList(contests, sportId))
                .doOnTerminate(() ->
                        log.info("Method execution time: {} second",(System.currentTimeMillis() - startTime) / 1000)
                );
    }

    private Mono<ContestDTO> processContest(Contest contest) {
        ContestDTO contestDTO = new ContestDTO(contest);

        return Mono.zip(
                        fetchSubject(contest.getSubjectId()),
                        fetchSport(contest.getTypeOfSportId()),
                        fetchTotalSubjects(contest.getId(), contest.getTotalSubjects())
                )
                .flatMap(tuple -> {
                    contestDTO.setSubject(tuple.getT1());
                    contestDTO.setSport(tuple.getT2());
                    contestDTO.setSubjects(tuple.getT3());
                    return fetchArchiveSports(contest.getASportIds())
                            .doOnNext(contestDTO::setSports)
                            .thenReturn(contestDTO);
                });
    }

    private Mono<SubjectDTO> fetchSubject(int subjectId) {
        return subjectService.getById(subjectId)
                .map(SubjectDTO::new);
    }

    private Mono<TypeOfSportDTO> fetchSport(int sportId) {
        return sportService.findById(sportId)
                .map(TypeOfSportDTO::new);
    }

    private Mono<List<SubjectDTO>> fetchTotalSubjects(long contestId, Set<Integer> subjectIds) {
        return contestService.getById(contestId)
                .flatMapMany(contest -> subjectService.getByIds(subjectIds))
                .map(SubjectDTO::new)
                .collectSortedList(Comparator.comparing(SubjectDTO::getTitle));
    }

    private Mono<List<SportDTO>> fetchArchiveSports(Set<Long> archiveSportIds) {
        Set<Long> convertedIds = new HashSet<>(archiveSportIds);

        return archiveSportService.getAllByIdIn(convertedIds)
                .flatMap(this::processArchiveSport)
                .sort(Comparator.comparing(SportDTO::getId))
                .collectList();
    }

    private Mono<SportDTO> processArchiveSport(ArchiveSport archiveSport) {
        SportDTO sportDTO = new SportDTO(archiveSport);

        return Mono.zip(
                        fetchDiscipline(archiveSport.getDisciplineId()),
                        fetchAgeGroup(archiveSport.getAgeGroupId()),
                        fetchPlaces(archiveSport.getPlaceIds())
                )
                .doOnNext(tuple -> {
                    sportDTO.setDiscipline(tuple.getT1());
                    sportDTO.setGroup(tuple.getT2());
                    sportDTO.setPlaces(tuple.getT3());
                })
                .thenReturn(sportDTO);
    }

    private Mono<DisciplineDTO> fetchDiscipline(int disciplineId) {
        return disciplineService.getById(disciplineId)
                .map(DisciplineDTO::new);
    }

    private Mono<AgeGroupDTO> fetchAgeGroup(int ageGroupId) {
        return groupService.getById(ageGroupId)
                .map(AgeGroupDTO::new);
    }

    private Mono<List<PlaceDTO>> fetchPlaces(Set<Long> placeIds) {
        return placeService.getAllByIdIn(placeIds)
                .flatMap(this::processPlace)
                .sort(Comparator.comparing(PlaceDTO::getPlace))
                .collectList();
    }

    private Mono<PlaceDTO> processPlace(Place place) {
        PlaceDTO placeDTO = new PlaceDTO(place);

        return Mono.zip(
                        fetchQualification(place.getQualificationId()),
                        fetchParticipant(place.getParticipantId()),
                        fetchSportSchool(place.getSportSchoolId())
                )
                .doOnNext(tuple -> {
                    placeDTO.setQualification(tuple.getT1());
                    placeDTO.setParticipant(tuple.getT2());
                    placeDTO.setSchool(tuple.getT3());
                })
                .thenReturn(placeDTO);
    }

    private Mono<QualificationDTO> fetchQualification(int qualificationId) {
        return qualificationService.getById(qualificationId)
                .map(QualificationDTO::new);
    }

    private Mono<ParticipantDTO> fetchParticipant(int participantId) {
        return participantService.getById(participantId)
                .map(ParticipantDTO::new);
    }

    private Mono<SportSchoolDTO> fetchSportSchool(int schoolId) {
        return schoolService.getById(schoolId)
                .flatMap(school ->
                        fetchSubject(school.getSubjectId())
                                .map(subjectDTO -> {
                                    SportSchoolDTO schoolDTO = new SportSchoolDTO(school);
                                    schoolDTO.setSubject(subjectDTO);
                                    return schoolDTO;
                                })
                );
    }

    private Mono<RestContest> processContestList(List<ContestDTO> contests, int sportId) {
        List<ContestDTO> filtered = sportId == -1
                ? contests
                : contests.stream()
                .filter(c -> c.getSportId() == sportId)
                .toList();

        List<ContestDTO> sorted = filtered.stream()
                .sorted(Comparator.comparing(ContestDTO::getSportTitle))
                .collect(Collectors.toList());

        RestContest restContest = new RestContest();
        restContest.setTotal(sorted.size());
        restContest.setContests(sorted);

        sorted.forEach(contest -> {
            SportFilterType type = contest.getSport().getSportFilterType();
            if (type == SportFilterType.OLYMPIC) restContest.getOlympic().add(contest);
            else if (type == SportFilterType.NO_OLYMPIC) restContest.getNoOlympic().add(contest);
            else if (type == SportFilterType.ADAPTIVE) restContest.getAdaptive().add(contest);
        });

        return Mono.just(restContest);
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
