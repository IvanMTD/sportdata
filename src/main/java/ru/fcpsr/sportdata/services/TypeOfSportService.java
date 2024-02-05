package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.TypeOfSport;
import ru.fcpsr.sportdata.repositories.TypeOfSportRepository;

@Service
@RequiredArgsConstructor
public class TypeOfSportService {
    private final TypeOfSportRepository sportRepository;

    public Flux<TypeOfSport> getAll(){
        return sportRepository.findAll();
    }
}
