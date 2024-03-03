package ru.fcpsr.sportdata.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.models.TypeOfSport;

import java.util.Set;

public interface SubjectRepository extends ReactiveCrudRepository<Subject,Integer> {
    Mono<Subject> findByTitle(String title);
    Flux<Subject> findAllByIdIn(Set<Integer> ids);

    @Query("select * from subject where title like :letter || '%'")
    Flux<Subject> findAllWhereFirstLetterIs(@Param("letter") String letter);
}
