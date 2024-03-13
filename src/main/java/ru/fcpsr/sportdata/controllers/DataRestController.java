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
import ru.fcpsr.sportdata.services.*;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/database/")
public class DataRestController {

    private final ParticipantService participantService;
    private final QualificationService qualificationService;
    private final TypeOfSportService sportService;
    private final SportSchoolService schoolService;
    private final SubjectService subjectService;

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
