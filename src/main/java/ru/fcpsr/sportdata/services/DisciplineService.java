package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Discipline;
import ru.fcpsr.sportdata.repositories.DisciplineRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRepository disciplineRepository;

    public Flux<Discipline> getAllByIds(Set<Integer> disciplineIds) {
        return disciplineRepository.findAllByIdIn(disciplineIds);
    }
}
