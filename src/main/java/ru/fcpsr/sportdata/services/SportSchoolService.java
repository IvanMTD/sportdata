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
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.dto.SportSchoolDTO;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.SportSchool;
import ru.fcpsr.sportdata.models.Subject;
import ru.fcpsr.sportdata.repositories.SportSchoolRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SportSchoolService {
    private final SportSchoolRepository schoolRepository;

    /**
     * FIND MONO
     */

    @Cacheable("schools")
    public Mono<SportSchool> findByTitle(String title) {
        return schoolRepository.findByTitle(title).defaultIfEmpty(new SportSchool());
    }

    /**
     * FIND FLUX
     */

    @Cacheable("schools")
    public Flux<SportSchool> getAll() {
        return schoolRepository.findAll();
    }

    @Cacheable("schools")
    public Flux<SportSchool> getAllByIdIn(Set<Integer> sportSchoolIds) {
        return schoolRepository.findAllByIdIn(sportSchoolIds);
    }

    /**
     * CREATE
     */

    @CachePut("schools")
    public Mono<SportSchool> saveSchool(SportSchoolDTO sportSchoolDTO) {
        return Mono.just(new SportSchool()).flatMap(sportSchool -> {
            sportSchool.setTitle(sportSchoolDTO.getTitle());
            sportSchool.setAddress(sportSchoolDTO.getAddress());
            sportSchool.setSubjectId(sportSchoolDTO.getSubjectId());
            return schoolRepository.save(sportSchool);
        });
    }

    /**
     * UPDATE
     */

    @CachePut("schools")
    public Mono<SportSchool> addParticipantInSchool(int id, Participant participant) {
        return schoolRepository.findById(id).flatMap(school -> {
            school.addParticipant(participant);
            return schoolRepository.save(school);
        });
    }

    @CachePut("schools")
    public Mono<SportSchool> removeParticipantFromSchool(int sid, int pid) {
        return schoolRepository.findById(sid).flatMap(school -> {
            school.getParticipantIds().remove(pid);
            return schoolRepository.save(school);
        });
    }


    @CachePut("schools")
    public Mono<SportSchool> updateTitleAndAddress(SportSchoolDTO sportSchoolDTO) {
        return schoolRepository.findById(sportSchoolDTO.getId()).flatMap(school -> {
            school.setTitle(sportSchoolDTO.getTitle());
            school.setAddress(sportSchoolDTO.getAddress());
            return schoolRepository.save(school);
        });
    }
    @CachePut("schools")
    public Mono<SportSchool> updateSchoolParticipant(int pid, int oldSchoolId, int schoolId) {
        return schoolRepository.findById(oldSchoolId).flatMap(school -> {
            school.getParticipantIds().remove(pid);
            return schoolRepository.save(school);
        }).flatMap(sportSchool -> schoolRepository.findById(schoolId).flatMap(school -> {
            school.addParticipantId(pid);
            return schoolRepository.save(school);
        }));
    }
    @CachePut("schools")
    public Flux<SportSchool> removeParticipantFromSchools(Participant participant) {
        return schoolRepository.findAllByIdIn(participant.getSportSchoolIds()).flatMap(sportSchool -> {
            sportSchool.getParticipantIds().remove(participant.getId());
            return schoolRepository.save(sportSchool);
        });
    }

    /**
     * DELETE
     */
    @CacheEvict("schools")
    public Mono<SportSchool> deleteSchool(int schoolId) {
        return schoolRepository.findById(schoolId).flatMap(sportSchool -> schoolRepository.delete(sportSchool).then(Mono.just(sportSchool)));
    }

    @CacheEvict("schools")
    public Flux<SportSchool> deleteSchoolsByIds(Set<Integer> sportSchoolIds) {
        return schoolRepository.findAllByIdIn(sportSchoolIds).flatMap(sportSchool -> schoolRepository.delete(sportSchool).then(Mono.just(sportSchool))).defaultIfEmpty(new SportSchool());
    }

    /**
     * COUNT
     * @return
     */

    public Mono<Long> getCount() {
        return schoolRepository.count();
    }

    @Cacheable("schools")
    public Mono<SportSchool> getById(int sportSchoolId) {
        return schoolRepository.findById(sportSchoolId).defaultIfEmpty(new SportSchool());
    }
}
