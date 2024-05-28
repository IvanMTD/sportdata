package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.fcpsr.sportdata.models.News;

public interface NewsRepository extends ReactiveCrudRepository<News,Long> {
}
