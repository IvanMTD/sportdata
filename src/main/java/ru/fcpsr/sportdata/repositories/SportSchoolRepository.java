package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.SportSchool;

import java.util.Set;

public interface SportSchoolRepository extends ReactiveCrudRepository<SportSchool,Integer> {
    Flux<SportSchool> findAllByIdIn(Set<Integer> sportSchoolIds);

    Mono<SportSchool> findByTitle(String title);
}
