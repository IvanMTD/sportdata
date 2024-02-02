package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.TypeOfSport;

import java.util.Iterator;
import java.util.List;

public interface TypeOfSportRepository extends ReactiveCrudRepository<TypeOfSport,Integer> {
    Mono<TypeOfSport> findByTitle(String title);
    Flux<TypeOfSport> findAllByTitleIn(List<String> titles);
}
