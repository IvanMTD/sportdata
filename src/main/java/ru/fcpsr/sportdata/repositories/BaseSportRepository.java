package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.BaseSport;

import java.util.Set;

public interface BaseSportRepository extends ReactiveCrudRepository<BaseSport,Integer> {
    Flux<BaseSport> findAllByIdIn(Set<Integer> baseSportIds);
}
