package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.repositories.ParticipantRepository;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    // FIND MONO

    // FIND FLUX
    public Flux<Participant> getAll(){
        return participantRepository.findAll();
    }
}
