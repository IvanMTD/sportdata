package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Qualification;

import java.util.Set;

public interface QualificationRepository extends ReactiveCrudRepository<Qualification,Integer> {

    Flux<Qualification> findAllByIdIn(Set<Integer> qualificationIds);
}
