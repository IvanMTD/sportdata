package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Contest;

public interface ContestRepository extends ReactiveCrudRepository<Contest,Integer> {
    Mono<Contest> findByEkp(String ekp);
}
