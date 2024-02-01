package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.Subject;

public interface SubjectRepository extends ReactiveCrudRepository<Subject,Integer> {

}
