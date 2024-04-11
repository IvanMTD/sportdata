package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.SubSportPartDTO;
import ru.fcpsr.sportdata.dto.SubjectDTO;
import ru.fcpsr.sportdata.models.BaseSport;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.SportSchool;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.repositories.SubjectRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    // GET MONO
    //@Cacheable("subjects")
    public Mono<Subject> findByTitle(String title) {
        return subjectRepository.findByTitle(title).defaultIfEmpty(new Subject());
    }

    //@Cacheable("subjects")
    public Mono<Subject> findByISO(String iso) {
        return subjectRepository.findByIso(iso).defaultIfEmpty(new Subject());
    }

    //@Cacheable("subjects")
    public Mono<Subject> getById(int subjectId) {
        return subjectRepository.findById(subjectId).defaultIfEmpty(new Subject());
    }
    // GET FLUX
    //@Cacheable("subjects")
    public Flux<Subject> getSubjectsByFirstLetter(String letter) {
        return subjectRepository.findAllWhereFirstLetterIs(letter);
    }
    //@Cacheable("subjects")
    public Flux<Subject> getByIds(Set<Integer> subjectIds) {
        return subjectRepository.findAllByIdIn(subjectIds).defaultIfEmpty(new Subject());
    }
    //@Cacheable("subjects")
    public Flux<Subject> getAll() {
        return subjectRepository.findAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Subject::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    // CREATE
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> save(SubjectDTO subjectDTO){
        Subject subject = new Subject();
        subject.setTitle(subjectDTO.getTitle());
        subject.setIso(subjectDTO.getIso());
        subject.setFederalDistrict(subjectDTO.getFederalDistrict());
        return subjectRepository.save(subject);
    }

    // UPDATE
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> addBaseSportInSubject(BaseSport baseSport) {
        return subjectRepository.findById(baseSport.getSubjectId()).flatMap(subject -> {
            subject.addBaseSport(baseSport);
            return subjectRepository.save(subject);
        });
    }

    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> removeBaseSportFromSubject(BaseSport baseSport) {
        return subjectRepository.findById(baseSport.getSubjectId()).flatMap(subject -> {
            subject.getBaseSportIds().remove(baseSport.getId());
            return subjectRepository.save(subject);
        });
    }
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> addSchoolInSubject(SportSchool sportSchool) {
        return subjectRepository.findById(sportSchool.getSubjectId()).flatMap(subject -> {
            subject.addSportSchool(sportSchool);
            return subjectRepository.save(subject);
        });
    }
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> updateTitle(SubjectDTO subjectDTO) {
        return subjectRepository.findById(subjectDTO.getId()).flatMap(subject -> {
            subject.setTitle(subjectDTO.getTitle());
            return subjectRepository.save(subject);
        });
    }
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> updateISO(SubjectDTO subjectDTO) {
        return subjectRepository.findById(subjectDTO.getId()).flatMap(subject -> {
            subject.setIso(subjectDTO.getIso());
            return subjectRepository.save(subject);
        });
    }
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> removeSchoolFromSubject(int subjectId, int schoolId) {
        return subjectRepository.findById(subjectId).flatMap(subject -> {
            subject.getSportSchoolIds().remove(schoolId);
            return subjectRepository.save(subject);
        });
    }

    // DELETE
    //@CacheEvict(value = "subjects", allEntries = true)
    public Mono<Subject> deleteSubject(int subjectId) {
        return subjectRepository.findById(subjectId).flatMap(subject -> subjectRepository.delete(subject).then(Mono.just(subject)));
    }

    public Mono<Long> getCount() {
        return subjectRepository.count();
    }
}
