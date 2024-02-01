package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.TypeOfSport;

public interface TypeOfSportRepository extends ReactiveCrudRepository<TypeOfSport,Integer> {

}
