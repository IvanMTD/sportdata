package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.dto.SubSportPartDTO;
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

    public Flux<Participant> findAllByLastname(String lastname) {
        return participantRepository.findAllByLastname(lastname);
    }

    public Flux<Participant> getAllByFirstLetter(String letter) {
        return participantRepository.findAllWhereFirstLetterIs(letter);
    }

    // UPDATE
    public Mono<Participant> addSubjectInParticipant(SubSportPartDTO ssDTO) {
        String str = ssDTO.getFullName();
        int spacePos = str.indexOf(" ");
        String lastname;
        if (spacePos > -1) { // Если пробел найден
            lastname= str.substring(0, spacePos);
        } else { // Если пробел не найден
            lastname = str;
        }
        return participantRepository.findAllByLastname(lastname).collectList().flatMap(participants -> {
            Participant p = null;
            for(Participant participant : participants){
                if(participant.getFullName().equals(ssDTO.getFullName())){
                    participant.addSubjectId(ssDTO.getSubjectId());
                    p = participant;
                    break;
                }
            }
            if(p != null){
                return participantRepository.save(p);
            }else{
                return Mono.empty();
            }
        });
    }

    public Mono<Participant> addSubjectInParticipantById(SubSportPartDTO sspDTO) {
        return participantRepository.findById(sspDTO.getParticipantId()).flatMap(participant -> {
            participant.addSubjectId(sspDTO.getSubjectId());
            return participantRepository.save(participant);
        });
    }

    // DELETE
    public Mono<Participant> deleteSubjectFromParticipant(int pid, int sid) {
        return participantRepository.findById(pid).flatMap(participant -> {
            participant.getSubjectIds().remove(sid);
            return participantRepository.save(participant);
        });
    }
}
