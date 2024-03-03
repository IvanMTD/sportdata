package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.FilterDTO;
import ru.fcpsr.sportdata.dto.SubjectDTO;
import ru.fcpsr.sportdata.dto.SubjectSportDTO;
import ru.fcpsr.sportdata.dto.TypeOfSportDTO;
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

    public Mono<TypeOfSport> addSubjectInSport(SubjectSportDTO ssDTO) {
        return sportRepository.findById(ssDTO.getSportId()).flatMap(sport -> {
            sport.addSubjectId(ssDTO.getSubjectId());
            return sportRepository.save(sport);
        });
    }
    // DELETE DATA
    public Mono<TypeOfSport> deleteSubjectFromSport(int sportId, int subjectId) {
        return sportRepository.findById(sportId).flatMap(sport -> {
            sport.getSubjectIds().remove(subjectId);
            return sportRepository.save(sport);
        });
    }
}
