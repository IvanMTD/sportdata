package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.Qualification;

public interface QualificationRepository extends ReactiveCrudRepository<Qualification,Integer> {

}
