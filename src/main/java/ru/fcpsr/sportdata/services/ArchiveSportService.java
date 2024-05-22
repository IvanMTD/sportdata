package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.ArchiveSport;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.repositories.ArchiveSportRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ArchiveSportService {
    private final ArchiveSportRepository archiveSportRepository;

    /**
     * CREATE
     * @param archiveSport
     * @return
     */
    //@CachePut("archiveSport")
    public Mono<ArchiveSport> saveArchiveSport(ArchiveSport archiveSport) {
        return archiveSportRepository.save(archiveSport);
    }

    //@Cacheable("archiveSport")
    public Flux<ArchiveSport> getAllByIdIn(Set<Integer> aSportIds) {
        return archiveSportRepository.findAllByIdIn(aSportIds).defaultIfEmpty(new ArchiveSport());
    }

    //@Cacheable("archiveSport")
    public Mono<ArchiveSport> getById(int id) {
        return archiveSportRepository.findById(id).defaultIfEmpty(new ArchiveSport());
    }

    //@CacheEvict(value = "archiveSport", allEntries = true)
    public Mono<ArchiveSport> deletePlaceFromASport(Place place) {
        return archiveSportRepository.findById(place.getASportId()).flatMap(archiveSport -> {
            archiveSport.getPlaceIds().remove(place.getId());
            return archiveSportRepository.save(archiveSport);
        });
    }

    public Flux<ArchiveSport> deleteAllCurrent(Set<Integer> aSportIds) {
        return archiveSportRepository
                .findAllByIdIn(aSportIds)
                .collectList()
                .flatMap(list -> archiveSportRepository.deleteAll(list).then(Mono.just(list)))
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<ArchiveSport> deleteBy(int id) {
        return archiveSportRepository.findById(id).flatMap(archiveSport -> {
            return archiveSportRepository.delete(archiveSport).then(Mono.just(archiveSport));
        });
    }
}
