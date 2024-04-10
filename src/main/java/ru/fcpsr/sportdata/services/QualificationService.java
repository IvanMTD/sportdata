package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.dto.QualificationDTO;
import ru.fcpsr.sportdata.models.Category;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.models.Qualification;
import ru.fcpsr.sportdata.repositories.QualificationRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class QualificationService {
    private final QualificationRepository qualificationRepository;

    // FIND MONO
    @Cacheable("qualifications")
    public Mono<Qualification> getById(int id) {
        return qualificationRepository.findById(id).defaultIfEmpty(new Qualification());
    }

    // FIND FLUX
    @Cacheable("qualifications")
    public Flux<Qualification> getAllByIds(Set<Integer> qualificationIds) {
        return qualificationRepository.findAllByIdIn(qualificationIds).defaultIfEmpty(new Qualification(Category.BR));
    }

    @Cacheable("qualifications")
    public Flux<Qualification> getAllBySportId(int sportId) {
        return qualificationRepository.findAllByTypeOfSportId(sportId);
    }
    // CREATE
    @CacheEvict(value = "qualifications", allEntries = true)
    public Mono<Qualification> createNewQualification(QualificationDTO qualificationDTO) {
        return Mono.just(new Qualification()).flatMap(qualification -> {
            qualification.setCategory(qualificationDTO.getCategory());
            qualification.setTypeOfSportId(qualificationDTO.getSportId());
            qualification.setParticipantId(qualificationDTO.getParticipantId());
            return qualificationRepository.save(qualification);
        });
    }

    @CacheEvict(value = "qualifications", allEntries = true)
    public Mono<Qualification> save(Qualification qualification) {
        return qualificationRepository.save(qualification);
    }
    // UPDATE
    @CacheEvict(value = "qualifications", allEntries = true)
    public Mono<Qualification> updateQualification(QualificationDTO qualificationDTO) {
        return qualificationRepository.findById(qualificationDTO.getId()).flatMap(qualification -> {
            qualification.setCategory(qualificationDTO.getCategory());
            qualification.setTypeOfSportId(qualificationDTO.getSportId());
            return qualificationRepository.save(qualification);
        });
    }

    // DELETE
    @CacheEvict(value = "qualifications", allEntries = true)
    public Mono<Qualification> deleteQualification(int qid) {
        return qualificationRepository.findById(qid).flatMap(qualification -> qualificationRepository.delete(qualification).then(Mono.just(qualification)));
    }

    @CacheEvict(value = "qualifications", allEntries = true)
    public Flux<Qualification> deleteQualifications(Set<Integer> qualificationIds) {
        return qualificationRepository.findAllByIdIn(qualificationIds).flatMap(qualification -> qualificationRepository.delete(qualification).then(Mono.just(qualification)));
    }

    // COUNT
    public Mono<Long> getCount() {
        return qualificationRepository.count();
    }
}
