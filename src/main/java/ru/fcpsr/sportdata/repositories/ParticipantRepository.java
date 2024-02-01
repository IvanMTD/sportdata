package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.Participant;

public interface ParticipantRepository extends ReactiveCrudRepository<Participant, Integer> {
    
}
