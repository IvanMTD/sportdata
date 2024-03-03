package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.FilterDTO;
import ru.fcpsr.sportdata.dto.TypeOfSportDTO;
import ru.fcpsr.sportdata.models.Discipline;
import ru.fcpsr.sportdata.models.TypeOfSport;
import ru.fcpsr.sportdata.repositories.TypeOfSportRepository;

@Service
@RequiredArgsConstructor
public class TypeOfSportService {
    private final TypeOfSportRepository sportRepository;

    // FIND MONO

    // FIND FLUX
    public Flux<TypeOfSport> getAll(){
        return sportRepository.findAll();
    }
    public Flux<TypeOfSport> getSportsByFirstLetter(String letter) {
        return sportRepository.findAllWhereFirstLetterIs(letter);
    }
    // UPDATE DATA
    public Mono<TypeOfSport> updateSportTitle(TypeOfSportDTO sportDTO) {
        return sportRepository.findById(sportDTO.getId()).flatMap(sport -> {
            sport.setTitle(sportDTO.getTitle());
            return sportRepository.save(sport);
        });
    }
    public Mono<TypeOfSport> updateFilters(FilterDTO filter) {
        return sportRepository.findById(filter.getSportId()).flatMap(sport -> {
            sport.setSeason(filter.getSeason());
            sport.setSportFilterType(filter.getFilter());
            return sportRepository.save(sport);
        });
    }

    public Mono<TypeOfSport> updateDisciplineInSport(Discipline discipline) {
        return sportRepository.findById(discipline.getTypeOfSportId()).flatMap(typeOfSport -> {
            typeOfSport.addDiscipline(discipline);
            return sportRepository.save(typeOfSport);
        });
    }
    // DELETE DATA




}
