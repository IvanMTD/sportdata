package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.SubjectDTO;
import ru.fcpsr.sportdata.dto.SubjectSportDTO;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.repositories.SubjectRepository;

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
        return subjectRepository.findAllByIdIn(subjectIds);
    }

    public Flux<Subject> getAll() {
        return subjectRepository.findAll();
    }

    public Mono<Subject> deleteSportFromSubject(int subjectId, int sportId) {
        return subjectRepository.findById(subjectId).flatMap(subject -> {
            subject.getTypeOfSportIds().remove(sportId);
            return subjectRepository.save(subject);
        });
    }

    public Mono<Subject> addSportInSubject(SubjectSportDTO ssDTO) {
        return subjectRepository.findById(ssDTO.getSubjectId()).flatMap(subject -> {
            subject.addSportId(ssDTO.getSportId());
            return subjectRepository.save(subject);
        });
    }
}
