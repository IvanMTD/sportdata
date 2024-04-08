package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ContestDTO;
import ru.fcpsr.sportdata.models.ArchiveSport;
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
    public Mono<ArchiveSport> saveArchiveSport(ArchiveSport archiveSport) {
        return archiveSportRepository.save(archiveSport);
    }

    public Flux<ArchiveSport> getAllByIdIn(Set<Integer> aSportIds) {
        return archiveSportRepository.findAllByIdIn(aSportIds).defaultIfEmpty(new ArchiveSport());
    }

    public Mono<ArchiveSport> getById(int id) {
        return archiveSportRepository.findById(id).defaultIfEmpty(new ArchiveSport());
    }
}
