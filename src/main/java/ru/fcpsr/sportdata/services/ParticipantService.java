package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.SportSchool;
import ru.fcpsr.sportdata.repositories.ParticipantRepository;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    // FIND MONO
    public Mono<Participant> findByFullNameAndBirthday(ParticipantDTO participantDTO) {
        if(participantDTO.getMiddleName() != null){
            return participantRepository.findByLastnameAndNameAndMiddleNameAndBirthday(participantDTO.getLastname(),participantDTO.getName(),participantDTO.getMiddleName(),participantDTO.getBirthday()).defaultIfEmpty(new Participant());
        }else{
            return participantRepository.findByLastnameAndNameAndBirthday(participantDTO.getLastname(),participantDTO.getName(),participantDTO.getBirthday()).defaultIfEmpty(new Participant());
        }
    }
    public Mono<Participant> getById(int participantId) {
        return participantRepository.findById(participantId);
    }
    // FIND FLUX
    public Flux<Participant> findAllByLastnameLike(String query) {
        return participantRepository.findParticipantsWithPartOfLastname("%" + query + "%").collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
    public Flux<Participant> getAllFreeParticipants(){
        return participantRepository.findAllFreeParticipants().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
    public Flux<Participant> getAll(){
        return participantRepository.findAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
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

    public Mono<Participant> findByFullName(String search) {
        String part[] = search.split(" ");
        if(part.length == 3){
            return participantRepository.findAllByLastnameAndNameAndMiddleName(part[0],part[1],part[2]).collectList().flatMap(participants -> {
                for(Participant participant : participants){
                    if(participant.getFullName().equals(search)){
                        return Mono.just(participant);
                    }
                }
                return Mono.empty();
            });
        }
        if(part.length == 2){
            return participantRepository.findAllByLastnameAndName(part[0],part[1]).collectList().flatMap(participants -> {
                for(Participant participant : participants){
                    if(participant.getFullName().equals(search)){
                        return Mono.just(participant);
                    }
                }
                return Mono.empty();
            });
        }
        return Mono.empty();
    }

    // CREATE
    public Mono<Participant> saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    public Mono<Participant> addNewParticipant(ParticipantDTO participantDTO) {
        return Mono.just(new Participant(participantDTO)).flatMap(participantRepository::save);
    }

    // UPDATE
    public Mono<Participant> removeSchoolFromParticipant(int pid, int sid) {
        return participantRepository.findById(pid).flatMap(participant -> {
            participant.getSportSchoolIds().remove(sid);
            return participantRepository.save(participant);
        });
    }
    public Flux<Participant> removeSchoolFromParticipants(SportSchool sportSchool) {
        return participantRepository.findAllByIdIn(sportSchool.getParticipantIds()).flatMap(participant -> {
            participant.getSportSchoolIds().remove(sportSchool.getId());
            return participantRepository.save(participant);
        }).defaultIfEmpty(new Participant());
    }

    public Mono<Participant> updateSchool(int id, int oldSchoolId, int schoolId) {
        return participantRepository.findById(id).flatMap(participant -> {
            participant.getSportSchoolIds().remove(oldSchoolId);
            participant.addSportSchoolId(schoolId);
            return participantRepository.save(participant);
        });
    }

    public Mono<Participant> updateSchool(int id, int schoolId) {
        return participantRepository.findById(id).flatMap(participant -> {
            participant.addSportSchoolId(schoolId);
            return participantRepository.save(participant);
        });
    }

    // COUNT
    public Mono<Long> getCount() {
        return participantRepository.count();
    }
}
