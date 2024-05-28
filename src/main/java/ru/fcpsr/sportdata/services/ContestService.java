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
import ru.fcpsr.sportdata.dto.ParticipantContestDTO;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.models.Contest;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.repositories.ContestRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContestService {
    private final ContestRepository contestRepository;

    /**
     * CREATE
     * @param contestDTO
     * @return
     */
    //@CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> addContest(ContestDTO contestDTO) {
        return Mono.just(new Contest(contestDTO)).flatMap(contestRepository::save);
    }

    //@CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> saveContest(Contest contest) {
        return contestRepository.save(contest);
    }

    //@Cacheable("contests")
    public Flux<Contest> getAll() {
        return contestRepository.findAll();
    }
    //@Cacheable("contests")
    public Mono<Contest> getContestByEkp(String ekp) {
        return contestRepository.findByEkp(ekp).defaultIfEmpty(new Contest());
    }

    //@CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> createContestFirstStep(ContestDTO contestDTO) {
        return Mono.just(new Contest()).flatMap(contest -> {
            contest.setTitle(contestDTO.getTitle());
            contest.setSportTitle(contestDTO.getSportTitle());
            contest.setSubjectTitle(contestDTO.getSubjectTitle());
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

    //@Cacheable("contests")
    public Mono<Contest> getById(int contestId) {
        return contestRepository.findById(contestId).defaultIfEmpty(new Contest());
    }

    //@CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> updateContestSecondStep(ContestDTO contestDTO) {
        return contestRepository.findById(contestDTO.getId()).flatMap(contest -> {
            contest.replaceSubjectIds(contestDTO);
            contest.updateContestData(contestDTO);
            return contestRepository.save(contest);
        });
    }

    //@CacheEvict(value = "contests", allEntries = true)
    public Mono<Contest> updateContestFirstStep(ContestDTO contestDTO) {
        return contestRepository.findById(contestDTO.getId()).flatMap(contest -> {
            contest.setTitle(contestDTO.getTitle());
            contest.setSportTitle(contestDTO.getSportTitle());
            contest.setSubjectTitle(contestDTO.getSubjectTitle());
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
    //@Cacheable("contests")
    public Flux<Contest> getAllSortedByDate(Pageable pageable) {
        return contestRepository.findAllByOrderByBeginningDesc(pageable);
    }

    public Flux<Contest> getAllSortedBySportTitle(Pageable pageable) {
        return contestRepository.findAllByOrderBySportTitle(pageable);
    }

    public Mono<Long> getCount() {
        return contestRepository.count();
    }

    public Mono<Long> getCountBy(String search){
        if(search.equals("all") || search.equals("")){
            return contestRepository.count();
        }else{
            List<Mono<Long>> fluxes = new ArrayList<>();
            fluxes.add(contestRepository.countByEkp(search));
            fluxes.add(contestRepository.countBySportTitle(search));
            fluxes.add(contestRepository.countBySubjectTitle(search));
            return Flux.merge(fluxes).collectList().flatMap(counts -> {
                long max = 0;
                for(long count : counts){
                    if(count > max){
                        max = count;
                    }
                }
                return Mono.just(max);
            });
        }
    }

    public Mono<Contest> deleteContest(int contestId) {
        return contestRepository.findById(contestId).flatMap(contest -> contestRepository.delete(contest).then(Mono.just(contest)));
    }

    public Flux<Contest> searchBy(String query) {
        String[] queryParts = query.split(" ");
        List<Flux<Contest>> fluxes = new ArrayList<>();
        for(String part : queryParts){
            String searchPart = "%" + part + "%";
            fluxes.add(contestRepository.findAllByEkpLikeIgnoreCase(searchPart));
            fluxes.add(contestRepository.findAllBySportTitleLikeIgnoreCase(searchPart));
            fluxes.add(contestRepository.findAllBySubjectTitleLikeIgnoreCase(searchPart));
        }

        return filterContest(fluxes,queryParts);
    }

    private Flux<Contest> filterContest(List<Flux<Contest>> fluxes, String[] searchParts){
        return Flux.merge(fluxes).distinct().flatMap(contest -> {
            int check = 0;
            for(String part : searchParts){
                if (contest.getEkp().toLowerCase().contains(part.toLowerCase())){
                    check++;
                }
                if (contest.getSportTitle().toLowerCase().contains(part.toLowerCase())){
                    check++;
                }
                if (contest.getSubjectTitle().toLowerCase().contains(part.toLowerCase())){
                    check++;
                }
            }
            if(check >= searchParts.length){
                return Mono.just(contest);
            }else{
                return Mono.empty();
            }
        });
    }

    public Flux<ContestDTO> getAllBy(Pageable pageable, String search) {
        List<Flux<Contest>> fluxes = new ArrayList<>();
        fluxes.add(contestRepository.findAllByEkp(pageable,search));
        fluxes.add(contestRepository.findAllBySportTitle(pageable,search));
        fluxes.add(contestRepository.findAllBySubjectTitle(pageable,search));
        return Flux.merge(fluxes).distinct().flatMap(contest -> Mono.just(new ContestDTO(contest)));
    }

    public Mono<Contest> deleteArchiveSportFromContest(ArchiveSport deleted) {
        return contestRepository.findById(deleted.getContestId()).flatMap(contest -> {
            contest.getASportIds().remove(deleted.getId());
            return contestRepository.save(contest);
        });
    }
}
