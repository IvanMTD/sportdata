package ru.fcpsr.sportdata.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Contest;

public interface ContestRepository extends ReactiveCrudRepository<Contest,Integer> {
    Mono<Contest> findByEkp(String ekp);

    Flux<Contest> findAllByOrderByBeginningDesc(Pageable pageable);
    Flux<Contest> findAllByEkpLikeIgnoreCase(String ekpPart);
    Flux<Contest> findAllBySportTitleLikeIgnoreCase(String sportPart);
    Flux<Contest> findAllBySubjectTitleLikeIgnoreCase(String sportPart);
}
