package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.AgeGroup;

public interface AgeGroupRepository extends ReactiveCrudRepository<AgeGroup,Integer> {

}
