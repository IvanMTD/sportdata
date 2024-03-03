package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.repositories.SubjectRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    // GET MONO
    // GET FLUX
    public Flux<Subject> getByIds(Set<Integer> subjectIds) {
        return subjectRepository.findAllByIdIn(subjectIds);
    }

    public Flux<Subject> getAll() {
        return subjectRepository.findAll();
    }
}
