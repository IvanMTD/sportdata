package ru.fcpsr.sportdata.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.TypeOfSport;

import java.time.LocalDate;
import java.util.Set;

public interface ParticipantRepository extends ReactiveCrudRepository<Participant, Integer> {

    Flux<Participant> findAllByIdIn(Set<Integer> ids);

    Flux<Participant> findAllByLastname(String lastname);
    @Query("select * from participant where lastname like :letter || '%'")
    Flux<Participant> findAllWhereFirstLetterIs(@Param("letter") String letter);

    @Query("select * from participant where sport_school_ids is null or cardinality(sport_school_ids) = 0")
    Flux<Participant> findAllFreeParticipants();

    @Query("SELECT * FROM Participant participant WHERE lastname LIKE :query or name LIKE :query")
    Flux<Participant> findParticipantsWithPartOfLastname(@Param("query") String query);

    Flux<Participant> findAllByIdInAndLastnameLikeIgnoreCase(Set<Integer> pidList, String lastnamePart);
    Flux<Participant> findAllByIdInAndNameLikeIgnoreCase(Set<Integer> pidList, String name);
    Flux<Participant> findAllByIdInAndMiddleNameLikeIgnoreCase(Set<Integer> pidList, String middleName);

    Flux<Participant> findAllByLastnameLikeIgnoreCase(String lastnamePart);
    Flux<Participant> findAllByNameLikeIgnoreCase(String name);
    Flux<Participant> findAllByMiddleNameLikeIgnoreCase(String middleName);

    Flux<Participant> findAllByLastnameAndNameAndMiddleName(String s, String s1, String s2);

    Flux<Participant> findAllByLastnameAndName(String s, String s1);

    Mono<Participant> findByLastnameAndNameAndBirthday(String lastname, String name, LocalDate birthday);

    Mono<Participant> findByLastnameAndNameAndMiddleNameAndBirthday(String lastname, String name, String middleName, LocalDate birthday);
}
