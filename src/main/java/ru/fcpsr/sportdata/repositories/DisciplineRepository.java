package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.Discipline;

public interface DisciplineRepository extends ReactiveCrudRepository<Discipline,Integer> {

}
