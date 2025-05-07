package ru.fcpsr.sportdata.services;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.SportSchoolDTO;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.SportSchool;
import ru.fcpsr.sportdata.repositories.SportSchoolRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SportSchoolService {
    private final SportSchoolRepository schoolRepository;

    /**
     * FIND MONO
     */
    public Mono<SportSchool> findByInn(String inn) {
        return schoolRepository.findByInn(inn).defaultIfEmpty(new SportSchool());
    }
    /**
     * FIND FLUX
     */

    //@Cacheable("schools")
    public Flux<SportSchool> getAll() {
        return schoolRepository.findAll();
    }

    //@Cacheable("schools")
    public Flux<SportSchool> getAllByIdIn(Set<Integer> sportSchoolIds) {
        return schoolRepository.findAllByIdIn(sportSchoolIds);
    }

    /**
     * CREATE
     */

    //@CacheEvict(value = "schools", allEntries = true)
    public Mono<SportSchool> saveSchool(SportSchoolDTO sportSchoolDTO) {
        return Mono.just(new SportSchool()).flatMap(sportSchool -> {
            sportSchool.setTitle(sportSchoolDTO.getTitle());
            sportSchool.setAddress(sportSchoolDTO.getAddress());
            sportSchool.setSubjectId(sportSchoolDTO.getSubjectId());
            sportSchool.setInn(sportSchoolDTO.getInn());
            return schoolRepository.save(sportSchool);
        });
    }

    /**
     * UPDATE
     */

    //@CacheEvict(value = "schools", allEntries = true)
    public Mono<SportSchool> addParticipantInSchool(int id, Participant participant) {
        return schoolRepository.findById(id).flatMap(school -> {
            school.addParticipant(participant);
            return schoolRepository.save(school);
        });
    }

    //@CacheEvict(value = "schools", allEntries = true)
    public Mono<SportSchool> removeParticipantFromSchool(int sid, int pid) {
        return schoolRepository.findById(sid).flatMap(school -> {
            school.getParticipantIds().remove(pid);
            return schoolRepository.save(school);
        });
    }


    //@CacheEvict(value = "schools", allEntries = true)
    public Mono<SportSchool> updateTitleAndAddress(SportSchoolDTO sportSchoolDTO) {
        System.out.println(sportSchoolDTO);
        return schoolRepository.findById(sportSchoolDTO.getId()).flatMap(school -> {
            school.setTitle(sportSchoolDTO.getTitle());
            school.setAddress(sportSchoolDTO.getAddress());
            school.setInn(sportSchoolDTO.getInn());
            return schoolRepository.save(school);
        });
    }
    //@CacheEvict(value = "schools", allEntries = true)
    public Mono<SportSchool> updateSchoolParticipant(int pid, int oldSchoolId, int schoolId) {
        return schoolRepository.findById(oldSchoolId).flatMap(school -> {
            school.getParticipantIds().remove(pid);
            return schoolRepository.save(school);
        }).flatMap(sportSchool -> schoolRepository.findById(schoolId).flatMap(school -> {
            school.addParticipantId(pid);
            return schoolRepository.save(school);
        }));
    }
    //@CacheEvict(value = "schools", allEntries = true)
    public Flux<SportSchool> removeParticipantFromSchools(Participant participant) {
        return schoolRepository.findAllByIdIn(participant.getSportSchoolIds()).flatMap(sportSchool -> {
            sportSchool.getParticipantIds().remove(participant.getId());
            return schoolRepository.save(sportSchool);
        });
    }

    /**
     * DELETE
     */
    //@CacheEvict(value = "schools", allEntries = true)
    public Mono<SportSchool> deleteSchool(int schoolId) {
        return schoolRepository.findById(schoolId).flatMap(sportSchool -> schoolRepository.delete(sportSchool).then(Mono.just(sportSchool)));
    }

    //@CacheEvict(value = "schools", allEntries = true)
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

    //@Cacheable(value = "schools")
    public Mono<SportSchool> getById(int sportSchoolId) {
        return schoolRepository.findById(sportSchoolId).defaultIfEmpty(new SportSchool());
    }

    //@Cacheable(value = "schools")
    public Mono<SportSchool> findByTitleAndSubjectId(SportSchoolDTO sportSchoolDTO) {
        return schoolRepository.findByTitleAndSubjectId(sportSchoolDTO.getTitle(),sportSchoolDTO.getSubjectId()).defaultIfEmpty(new SportSchool());
    }

    public Flux<SportSchool> getAllBySubjectIdAndTitle(int subjectId, String query) {
        String request = "%" + query + "%";
        Flux<SportSchool> schoolByTitle = schoolRepository.findAllByTitleLikeIgnoreCaseAndSubjectId(request,subjectId);
        Flux<SportSchool> schoolByInn = schoolRepository.findAllByInnAndSubjectId(request,subjectId);

        return Flux.merge(List.of(schoolByTitle, schoolByInn));
     }

    public Mono<SportSchool> saveSchool(SportSchool s) {
        return schoolRepository.save(s);
    }
}
