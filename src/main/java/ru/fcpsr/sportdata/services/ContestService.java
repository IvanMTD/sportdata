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

    public Mono<Contest> getById(int contestId) {
        return contestRepository.findById(contestId).defaultIfEmpty(new Contest());
    }

    public Mono<Contest> updateContestSecondStep(ContestDTO contestDTO) {
        return contestRepository.findById(contestDTO.getId()).flatMap(contest -> {
            contest.replaceSubjectIds(contestDTO);
            contest.updateContestData(contestDTO);
            return contestRepository.save(contest);
        });
    }

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
}
