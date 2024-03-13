package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
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
    public Mono<Contest> addContest(ContestDTO contestDTO) {
        return Mono.just(new Contest(contestDTO)).flatMap(contestRepository::save);
    }

    public Mono<Contest> saveContest(Contest contest) {
        return contestRepository.save(contest);
    }

    public Flux<Contest> getAll() {
        return contestRepository.findAll();
    }

    public Mono<Contest> getContestByEkp(String ekp) {
        return contestRepository.findByEkp(ekp).defaultIfEmpty(new Contest());
    }
}
