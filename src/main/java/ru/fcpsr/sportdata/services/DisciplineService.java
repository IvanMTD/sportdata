package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.DisciplineDTO;
import ru.fcpsr.sportdata.models.AgeGroup;
import ru.fcpsr.sportdata.models.Discipline;
import ru.fcpsr.sportdata.repositories.DisciplineRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisciplineService {
    private final DisciplineRepository disciplineRepository;

    // FIND MONO
    @Cacheable("disciplines")
    public Mono<Discipline> getById(int disciplineId) {
        return disciplineRepository.findById(disciplineId).defaultIfEmpty(new Discipline());
    }
    // FIND FLUX
    @Cacheable("disciplines")
    public Flux<Discipline> getAllByIds(Set<Integer> disciplineIds) {
        return disciplineRepository.findAllByIdIn(disciplineIds);
    }

    @Cacheable("disciplines")
    public Flux<Discipline> getAll() {
        return disciplineRepository.findAll();
    }

    // CREATE
    @CacheEvict(value = "disciplines", allEntries = true)
    public Mono<Discipline> addNew(DisciplineDTO disciplineDTO) {
        return Mono.just(new Discipline()).flatMap(discipline -> {
            discipline.setTypeOfSportId(disciplineDTO.getSportId());
            discipline.setTitle(disciplineDTO.getTitle());
            return disciplineRepository.save(discipline);
        });
    }

    // UPDATE
    @CacheEvict(value = "disciplines", allEntries = true)
    public Mono<Discipline> updateTitle(DisciplineDTO disciplineDTO) {
        return disciplineRepository.findById(disciplineDTO.getId()).flatMap(discipline -> {
            discipline.setTitle(disciplineDTO.getTitle());
            return disciplineRepository.save(discipline);
        });
    }

    // DELETE
    @CacheEvict(value = "disciplines", allEntries = true)
    public Mono<Discipline> deleteDiscipline(int disciplineId) {
        return disciplineRepository.findById(disciplineId).flatMap(discipline -> disciplineRepository.delete(discipline).then(Mono.just(discipline)));
    }

    // COUNT
    public Mono<Long> getCount() {
        return disciplineRepository.getCount();
    }

    @Cacheable("disciplines")
    public Flux<Discipline> getAllBySportId(int sportId) {
        return disciplineRepository.findAllByTypeOfSportId(sportId).collectList().flatMapMany(dl -> {
            dl = dl.stream().sorted(Comparator.comparing(Discipline::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(dl);
        }).flatMapSequential(Mono::just);
    }

   /* public Mono<Discipline> updateGroupInDiscipline(AgeGroup group) {
        return disciplineRepository.findById(group.getDisciplineId()).flatMap(discipline -> {
            return disciplineRepository.save(discipline);
        });
    }*/
}
