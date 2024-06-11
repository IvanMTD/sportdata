package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Place;

import java.util.List;
import java.util.Set;

public interface PlaceRepository extends ReactiveCrudRepository<Place, Integer> {
    Mono<Place> findById(long id);
    Flux<Place> findAllByIdIn(Set<Long> placeIds);
    Flux<Place> findAllByIdIn(List<Long> placeIds);

    Flux<Place> findAllByParticipantId(int participantId);
}
