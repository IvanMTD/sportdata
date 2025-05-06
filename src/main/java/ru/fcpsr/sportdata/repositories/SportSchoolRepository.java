package ru.fcpsr.sportdata.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.SportSchool;

import java.util.Set;

public interface SportSchoolRepository extends ReactiveCrudRepository<SportSchool,Integer> {
    Flux<SportSchool> findAllByIdIn(Set<Integer> sportSchoolIds);

    Mono<SportSchool> findByTitle(String title);
    @Query("select * from sport_school where :pid = any(participant_ids)")
    Flux<SportSchool> findAllWhereParticipantIdAny(@Param("pid") int pid);

    Mono<SportSchool> findByTitleAndSubjectId(String title, int subjectId);

    Flux<SportSchool> findAllByTitleLikeIgnoreCaseAndSubjectId(String title, int subjectId);

    Flux<SportSchool> findAllByInnAndSubjectId(String request, int subjectId);
}
