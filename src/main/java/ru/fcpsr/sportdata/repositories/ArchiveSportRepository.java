package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.ArchiveSport;

import java.util.Set;

public interface ArchiveSportRepository extends ReactiveCrudRepository<ArchiveSport,Integer> {
    Flux<ArchiveSport> findAllByIdIn(Set<Long> aSportIds);
    Mono<ArchiveSport> findById(long id);
}
