package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Place;

import java.util.Set;

public interface PlaceRepository extends ReactiveCrudRepository<Place, Integer> {
    Flux<Place> findAllByIdIn(Set<Integer> placeIds);
}
