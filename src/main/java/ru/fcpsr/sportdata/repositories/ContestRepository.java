package ru.fcpsr.sportdata.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Contest;

import java.time.LocalDate;

public interface ContestRepository extends ReactiveCrudRepository<Contest,Integer> {
    Mono<Contest> findById(long id);
    Mono<Contest> findByEkp(String ekp);
    Flux<Contest> findAllByEkp(Pageable pageable, String ekp);

    Flux<Contest> findAllByOrderByBeginningDesc(Pageable pageable);
    Flux<Contest> findAllByOrderBySportTitle(Pageable pageable);
    Flux<Contest> findAllByEkpLikeIgnoreCase(String ekpPart);
    Flux<Contest> findAllBySportTitleLikeIgnoreCase(String sportPart);
    Flux<Contest> findAllBySubjectTitleLikeIgnoreCase(String sportPart);
    Mono<Long> countBySportTitle(String sportTitle);
    Mono<Long> countBySubjectTitle(String subjectTitle);
    Mono<Long> countByEkp(String ekp);

    Flux<Contest> findAllBySportTitle(Pageable pageable, String search);
    Flux<Contest> findAllBySubjectTitle(Pageable pageable, String search);

    Flux<Contest> findAllByBeginningBetween(LocalDate startOfYear, LocalDate endOfYear);
}
