package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
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
    public Mono<Qualification> getById(int id) {
        return qualificationRepository.findById(id).defaultIfEmpty(new Qualification());
    }

    // FIND FLUX
    public Flux<Qualification> getAllByIds(Set<Integer> qualificationIds) {
        return qualificationRepository.findAllByIdIn(qualificationIds).defaultIfEmpty(new Qualification(Category.BR));
    }

    public Flux<Qualification> getAllBySportId(int sportId) {
        return qualificationRepository.findAllByTypeOfSportId(sportId);
    }
    // CREATE
    public Mono<Qualification> createNewQualification(QualificationDTO qualificationDTO) {
        return Mono.just(new Qualification()).flatMap(qualification -> {
            qualification.setCategory(qualificationDTO.getCategory());
            qualification.setTypeOfSportId(qualificationDTO.getSportId());
            qualification.setParticipantId(qualificationDTO.getParticipantId());
            return qualificationRepository.save(qualification);
        });
    }

    public Mono<Qualification> save(Qualification qualification) {
        return qualificationRepository.save(qualification);
    }
    // UPDATE
    public Mono<Qualification> updateQualification(QualificationDTO qualificationDTO) {
        return qualificationRepository.findById(qualificationDTO.getId()).flatMap(qualification -> {
            qualification.setCategory(qualificationDTO.getCategory());
            qualification.setTypeOfSportId(qualificationDTO.getSportId());
            return qualificationRepository.save(qualification);
        });
    }

    // DELETE
    public Mono<Qualification> deleteQualification(int qid) {
        return qualificationRepository.findById(qid).flatMap(qualification -> qualificationRepository.delete(qualification).then(Mono.just(qualification)));
    }

    public Flux<Qualification> deleteQualifications(Set<Integer> qualificationIds) {
        return qualificationRepository.findAllByIdIn(qualificationIds).flatMap(qualification -> qualificationRepository.delete(qualification).then(Mono.just(qualification)));
    }

    // COUNT
    public Mono<Long> getCount() {
        return qualificationRepository.count();
    }
}
