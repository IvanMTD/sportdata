package ru.fcpsr.sportdata.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.TypeOfSport;

import java.util.Set;

public interface ParticipantRepository extends ReactiveCrudRepository<Participant, Integer> {

    Flux<Participant> findAllByIdIn(Set<Integer> ids);

    Flux<Participant> findAllByLastname(String lastname);
    @Query("select * from participant where lastname like :letter || '%'")
    Flux<Participant> findAllWhereFirstLetterIs(@Param("letter") String letter);
}
