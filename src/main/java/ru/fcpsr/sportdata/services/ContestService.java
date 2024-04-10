package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ContestDTO;
import ru.fcpsr.sportdata.models.Contest;
import ru.fcpsr.sportdata.repositories.ContestRepository;

@Service
@RequiredArgsConstructor
public class ContestService {
    private final ContestRepository contestRepository;

    /**
     * CREATE
     * @param contestDTO
     * @return
     */
    @CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> addContest(ContestDTO contestDTO) {
        return Mono.just(new Contest(contestDTO)).flatMap(contestRepository::save);
    }

    @CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> saveContest(Contest contest) {
        return contestRepository.save(contest);
    }

    @Cacheable("contests")
    public Flux<Contest> getAll() {
        return contestRepository.findAll();
    }
    //@Cacheable("contests")
    public Mono<Contest> getContestByEkp(String ekp) {
        return contestRepository.findByEkp(ekp).defaultIfEmpty(new Contest());
    }

    @CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> createContestFirstStep(ContestDTO contestDTO) {
        return Mono.just(new Contest()).flatMap(contest -> {
            contest.setTitle(contestDTO.getTitle());
            contest.setEkp(contestDTO.getEkp());
            contest.setTypeOfSportId(contestDTO.getSportId());
            contest.setBeginning(contestDTO.getBeginning());
            contest.setEnding(contestDTO.getEnding());
            contest.setSubjectId(contestDTO.getSubjectId());
            contest.setCity(contestDTO.getCity());
            contest.setLocation(contestDTO.getLocation());
            return contestRepository.save(contest);
        });
    }

    @Cacheable("contests")
    public Mono<Contest> getById(int contestId) {
        return contestRepository.findById(contestId).defaultIfEmpty(new Contest());
    }

    @CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> updateContestSecondStep(ContestDTO contestDTO) {
        return contestRepository.findById(contestDTO.getId()).flatMap(contest -> {
            contest.replaceSubjectIds(contestDTO);
            contest.updateContestData(contestDTO);
            return contestRepository.save(contest);
        });
    }

    @CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> updateContestFirstStep(ContestDTO contestDTO) {
        return contestRepository.findById(contestDTO.getId()).flatMap(contest -> {
            contest.setTitle(contestDTO.getTitle());
            contest.setEkp(contestDTO.getEkp());
            contest.setTypeOfSportId(contestDTO.getSportId());
            contest.setBeginning(contestDTO.getBeginning());
            contest.setEnding(contestDTO.getEnding());
            contest.setSubjectId(contestDTO.getSubjectId());
            contest.setCity(contestDTO.getCity());
            contest.setLocation(contestDTO.getLocation());
            return contestRepository.save(contest);
        });
    }
    @Cacheable("contests")
    public Flux<Contest> getAllSortedByDate(Pageable pageable) {
        return contestRepository.findAllByOrderByBeginningDesc(pageable);
    }

    public Mono<Long> getCount() {
        return contestRepository.count();
    }
}
