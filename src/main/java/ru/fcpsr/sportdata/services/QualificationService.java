package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Qualification;
import ru.fcpsr.sportdata.repositories.QualificationRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class QualificationService {
    private final QualificationRepository qualificationRepository;

    // FIND MONO

    // FIND FLUX
    public Flux<Qualification> getAllByIds(Set<Integer> qualificationIds) {
        return qualificationRepository.findAllByIdIn(qualificationIds).defaultIfEmpty(new Qualification(Category.BR));
    }
    // CREATE

    // UPDATE

    // DELETE

    // COUNT
    public Mono<Long> getCount() {
        return qualificationRepository.count();
    }
}
