package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Qualification;

import java.util.Set;

public interface QualificationRepository extends ReactiveCrudRepository<Qualification,Integer> {

    Flux<Qualification> findAllByIdIn(Set<Integer> qualificationIds);

    Flux<Qualification> findAllByTypeOfSportId(int sportId);

    Mono<Qualification> findByTypeOfSportIdAndCategoryAndParticipantId(int sid, Category category, int pid);
}
