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
import ru.fcpsr.sportdata.dto.BaseSportDTO;
import ru.fcpsr.sportdata.dto.SubjectDTO;
import ru.fcpsr.sportdata.models.BaseSport;
import ru.fcpsr.sportdata.repositories.BaseSportRepository;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BaseSportService {
    private final BaseSportRepository baseSportRepository;

    /**
     * FIND MONO
     */

    /**
     * FIND FLUX
     */
    //@Cacheable("baseSports")
    public Flux<BaseSport> getAllByIds(Set<Integer> baseSportIds) {
        return baseSportRepository.findAllByIdIn(baseSportIds);
    }
    //@Cacheable("baseSports")
    public Flux<BaseSport> getAllByIdsWhereNotErrors(Set<Integer> baseSportIds) {
        return baseSportRepository.findAllByIdIn(baseSportIds).flatMap(baseSport -> {
            if(baseSport.getExpiration() > LocalDate.now().getYear()){
                return Mono.just(baseSport);
            }else{
                return Mono.empty();
            }
        });
    }

    /**
     * CREATE
     */
    //@CacheEvict(value = "baseSports", allEntries = true)
    public Mono<BaseSport> addNewBaseSport(BaseSportDTO baseSportDTO) {
        return Mono.just(new BaseSport()).flatMap(baseSport -> {
            baseSport.setTypeOfSportId(baseSportDTO.getSportId());
            baseSport.setSubjectId(baseSportDTO.getSubjectId());
            baseSport.setIssueDate(baseSportDTO.getIssueDate());
            baseSport.setExpiration(baseSportDTO.getExpiration());
            return baseSportRepository.save(baseSport);
        });
    }

    /**
     * UPDATE
     */

    /**
     * DELETE
     */

    //@CacheEvict(value = "baseSports", allEntries = true)
    public Mono<BaseSport> deleteBaseSport(int bSportId) {
        return baseSportRepository.findById(bSportId).flatMap(baseSport -> baseSportRepository.delete(baseSport).then(Mono.just(baseSport)));
    }

    //@CacheEvict(value = "baseSports", allEntries = true)
    public Flux<BaseSport> deleteAllBaseSports(Set<Integer> baseSportIds) {
        return baseSportRepository.findAllByIdIn(baseSportIds).flatMap(baseSport -> baseSportRepository.delete(baseSport).then(Mono.just(baseSport))).defaultIfEmpty(new BaseSport());
    }

    public Mono<Long> getCount() {
        return baseSportRepository.count();
    }
}
