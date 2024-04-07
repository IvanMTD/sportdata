package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.AgeGroup;

import java.util.List;
import java.util.Set;

public interface AgeGroupRepository extends ReactiveCrudRepository<AgeGroup,Integer> {
    Flux<AgeGroup> findAllByIdIn(List<Integer> ids);
    Flux<AgeGroup> findAllByIdIn(Set<Integer> ids);

    Flux<AgeGroup> findAllByTypeOfSportId(int sportId);
}
