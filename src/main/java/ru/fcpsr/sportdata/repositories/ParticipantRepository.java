package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Participant;

import java.util.Set;

public interface ParticipantRepository extends ReactiveCrudRepository<Participant, Integer> {

    Flux<Participant> findAllByIdIn(Set<Integer> ids);
}
