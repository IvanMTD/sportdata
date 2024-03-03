package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Subject;

import java.util.Set;

public interface SubjectRepository extends ReactiveCrudRepository<Subject,Integer> {
    Mono<Subject> findByTitle(String title);
    Flux<Subject> findAllByIdIn(Set<Integer> ids);
}
