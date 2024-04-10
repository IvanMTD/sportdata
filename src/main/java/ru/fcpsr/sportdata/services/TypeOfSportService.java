package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.FilterDTO;
import ru.fcpsr.sportdata.dto.QualificationDTO;
import ru.fcpsr.sportdata.dto.SubjectDTO;
import ru.fcpsr.sportdata.dto.TypeOfSportDTO;
import ru.fcpsr.sportdata.models.*;
import ru.fcpsr.sportdata.repositories.TypeOfSportRepository;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeOfSportService {
    private final TypeOfSportRepository sportRepository;

    // FIND MONO
    @Cacheable(value = "typeOfSports")
    public Mono<TypeOfSport> findSportByTitle(String title) {
        return sportRepository.findByTitle(title);
    }
    public Mono<TypeOfSport> findByIds(int typeOfSportId) {
        return sportRepository.findById(typeOfSportId).defaultIfEmpty(new TypeOfSport());
    }
    @Cacheable(value = "typeOfSports")
    public Mono<TypeOfSport> getById(int sportId) {
        return sportRepository.findById(sportId);
    }
    // FIND FLUX
    @Cacheable(value = "typeOfSports")
    public Flux<TypeOfSport> getAll(){
        return sportRepository.findAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(TypeOfSport::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
    @Cacheable(value = "typeOfSports")
    public Flux<TypeOfSport> getSportsByFirstLetter(String letter) {
        return sportRepository.findAllWhereFirstLetterIs(letter);
    }
    @Cacheable(value = "typeOfSports")
    public Flux<TypeOfSport> findByIds(Set<Integer> ids) {
        return sportRepository.findByIdIn(ids);
    }

    // CREATE
    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> addNewSport(TypeOfSportDTO sportDTO) {
        TypeOfSport sport = new TypeOfSport();
        sport.setTitle(sportDTO.getTitle());
        sport.setSeason(sportDTO.getSeason());
        sport.setSportFilterType(sportDTO.getSportFilterType());
        return sportRepository.save(sport);
    }

    // UPDATE DATA
    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> updateSportTitle(TypeOfSportDTO sportDTO) {
        return sportRepository.findById(sportDTO.getId()).flatMap(sport -> {
            sport.setTitle(sportDTO.getTitle());
            return sportRepository.save(sport);
        });
    }
    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> updateFilters(FilterDTO filter) {
        return sportRepository.findById(filter.getSportId()).flatMap(sport -> {
            sport.setSeason(filter.getSeason());
            sport.setSportFilterType(filter.getFilter());
            return sportRepository.save(sport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> updateDisciplineInSport(Discipline discipline) {
        return sportRepository.findById(discipline.getTypeOfSportId()).flatMap(typeOfSport -> {
            typeOfSport.addDiscipline(discipline);
            return sportRepository.save(typeOfSport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> deleteDisciplineFromSport(Discipline discipline) {
        return sportRepository.findById(discipline.getTypeOfSportId()).flatMap(sport -> {
            sport.getDisciplineIds().remove(discipline.getId());
            return sportRepository.save(sport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> addGroupInSport(AgeGroup group) {
        return sportRepository.findById(group.getTypeOfSportId()).flatMap(sport -> {
            sport.addAgeGroup(group);
            return sportRepository.save(sport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> addBaseSportInSport(BaseSport baseSport) {
        return sportRepository.findById(baseSport.getTypeOfSportId()).flatMap(sport -> {
            sport.addBaseSport(baseSport);
            return sportRepository.save(sport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> addQualificationInSport(QualificationDTO qualificationDTO) {
        return sportRepository.findById(qualificationDTO.getSportId()).flatMap(sport -> {
            sport.addQualificationId(qualificationDTO.getId());
            return sportRepository.save(sport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> addQualificationInSport(Qualification qualification) {
        return sportRepository.findById(qualification.getTypeOfSportId()).flatMap(sport -> {
            sport.addQualification(qualification);
            return sportRepository.save(sport);
        });
    }

    // DELETE
    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> deleteGroupFromSport(AgeGroup group) {
        return sportRepository.findById(group.getTypeOfSportId()).flatMap(sport -> {
            sport.getAgeGroupIds().remove(group.getId());
            return sportRepository.save(sport);
        });
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> deleteBaseSportFromSport(BaseSport baseSport) {
        return sportRepository.findById(baseSport.getTypeOfSportId()).flatMap(sport -> {
            sport.getBaseSportIds().remove(baseSport.getId());
            return sportRepository.save(sport);
        }).defaultIfEmpty(new TypeOfSport());
    }

    @CacheEvict(value = "typeOfSports", allEntries = true)
    public Mono<TypeOfSport> removeQualificationFromSport(Qualification qualification) {
        return sportRepository.findById(qualification.getTypeOfSportId()).flatMap(sport -> {
            sport.getQualificationIds().remove(qualification.getId());
            return sportRepository.save(sport);
        });
    }

    // COUNT
    public Mono<Long> getCount() {
        return sportRepository.getCount();
    }

    @Cacheable(value = "typeOfSports")
    public Mono<TypeOfSport> findById(int sportId) {
        return sportRepository.findById(sportId);
    }
}
