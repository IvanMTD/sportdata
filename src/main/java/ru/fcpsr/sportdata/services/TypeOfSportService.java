package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.FilterDTO;
import ru.fcpsr.sportdata.dto.TypeOfSportDTO;
import ru.fcpsr.sportdata.models.AgeGroup;
import ru.fcpsr.sportdata.models.BaseSport;
import ru.fcpsr.sportdata.models.Discipline;
import ru.fcpsr.sportdata.models.TypeOfSport;
import ru.fcpsr.sportdata.repositories.TypeOfSportRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TypeOfSportService {
    private final TypeOfSportRepository sportRepository;

    // FIND MONO
    public Mono<TypeOfSport> findSportByTitle(String title) {
        return sportRepository.findByTitle(title);
    }
    public Mono<TypeOfSport> findByIds(int typeOfSportId) {
        return sportRepository.findById(typeOfSportId).defaultIfEmpty(new TypeOfSport());
    }
    public Mono<TypeOfSport> getById(int sportId) {
        return sportRepository.findById(sportId);
    }
    // FIND FLUX
    public Flux<TypeOfSport> getAll(){
        return sportRepository.findAll();
    }
    public Flux<TypeOfSport> getSportsByFirstLetter(String letter) {
        return sportRepository.findAllWhereFirstLetterIs(letter);
    }
    public Flux<TypeOfSport> findByIds(Set<Integer> ids) {
        return sportRepository.findByIdIn(ids);
    }

    // CREATE
    public Mono<TypeOfSport> addNewSport(TypeOfSportDTO sportDTO) {
        TypeOfSport sport = new TypeOfSport();
        sport.setTitle(sportDTO.getTitle());
        sport.setSeason(sportDTO.getSeason());
        sport.setSportFilterType(sportDTO.getSportFilterType());
        return sportRepository.save(sport);
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

    public Mono<TypeOfSport> deleteDisciplineFromSport(Discipline discipline) {
        return sportRepository.findById(discipline.getTypeOfSportId()).flatMap(sport -> {
            sport.getDisciplineIds().remove(discipline.getId());
            return sportRepository.save(sport);
        });
    }

    public Mono<TypeOfSport> addGroupInSport(AgeGroup group) {
        return sportRepository.findById(group.getTypeOfSportId()).flatMap(sport -> {
            sport.addAgeGroup(group);
            return sportRepository.save(sport);
        });
    }

    public Mono<TypeOfSport> addBaseSportInSport(BaseSport baseSport) {
        return sportRepository.findById(baseSport.getTypeOfSportId()).flatMap(sport -> {
            sport.addBaseSport(baseSport);
            return sportRepository.save(sport);
        });
    }

    // DELETE
    public Mono<TypeOfSport> deleteGroupFromSport(AgeGroup group) {
        return sportRepository.findById(group.getTypeOfSportId()).flatMap(sport -> {
            sport.getAgeGroupIds().remove(group.getId());
            return sportRepository.save(sport);
        });
    }

    public Mono<TypeOfSport> deleteBaseSportFromSport(BaseSport baseSport) {
        return sportRepository.findById(baseSport.getTypeOfSportId()).flatMap(sport -> {
            sport.getBaseSportIds().remove(baseSport.getId());
            return sportRepository.save(sport);
        }).defaultIfEmpty(new TypeOfSport());
    }

    // COUNT
    public Mono<Long> getCount() {
        return sportRepository.getCount();
    }
}
