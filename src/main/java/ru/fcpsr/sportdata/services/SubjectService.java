package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.SubSportPartDTO;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.repositories.SubjectRepository;

import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    // GET MONO
    // GET FLUX
    public Flux<Subject> getSubjectsByFirstLetter(String letter) {
        return subjectRepository.findAllWhereFirstLetterIs(letter);
    }
    public Flux<Subject> getByIds(Set<Integer> subjectIds) {
        return subjectRepository.findAllByIdIn(subjectIds).defaultIfEmpty(new Subject());
    }

    public Flux<Subject> getAll() {
        return subjectRepository.findAll();
    }

    // DELETE
    public Mono<Subject> deleteSportFromSubject(int subjectId, int sportId) {
        return subjectRepository.findById(subjectId).flatMap(subject -> {
            subject.getTypeOfSportIds().remove(sportId);
            return subjectRepository.save(subject);
        });
    }

    public Mono<Subject> deleteParticipantFromSubject(int subjectId, int pid) {
        return subjectRepository.findById(subjectId).flatMap(subject -> {
            subject.getParticipantIds().remove(pid);
            return subjectRepository.save(subject);
        });
    }

    // CREATE
    public Mono<Subject> addSportInSubject(SubSportPartDTO ssDTO) {
        return subjectRepository.findById(ssDTO.getSubjectId()).flatMap(subject -> {
            subject.addSportId(ssDTO.getSportId());
            return subjectRepository.save(subject);
        });
    }

    // UPDATE
    public Mono<Subject> addParticipantInSubject(SubSportPartDTO ssDTO, Participant participant) {
        return subjectRepository.findById(ssDTO.getSubjectId()).flatMap(subject -> {
            subject.addParticipantId(participant.getId());
            return subjectRepository.save(subject);
        });
    }
}
