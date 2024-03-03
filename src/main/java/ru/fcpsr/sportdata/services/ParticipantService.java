package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.repositories.ParticipantRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    // FIND MONO

    // FIND FLUX
    public Flux<Participant> getAll(){
        return participantRepository.findAll();
    }
    public Flux<Participant> findByIds(Set<Integer> ids) {
        return participantRepository.findAllByIdIn(ids);
    }
}
