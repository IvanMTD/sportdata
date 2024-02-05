package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Discipline;

import java.util.Set;

public interface DisciplineRepository extends ReactiveCrudRepository<Discipline,Integer> {
    Flux<Discipline> findAllByIdIn(Set<Integer> ids);
}
