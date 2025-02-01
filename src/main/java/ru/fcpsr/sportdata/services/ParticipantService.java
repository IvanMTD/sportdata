package ru.fcpsr.sportdata.services;

import jakarta.mail.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.ParticipantDTO;
import ru.fcpsr.sportdata.models.Participant;
import ru.fcpsr.sportdata.models.Qualification;
import ru.fcpsr.sportdata.models.SportSchool;
import ru.fcpsr.sportdata.repositories.ParticipantRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    // FIND MONO
    //@Cacheable("participants")
    public Mono<Participant> findByFullNameAndBirthday(ParticipantDTO participantDTO) {
        if(participantDTO.getMiddleName() != null){
            return participantRepository.findByLastnameAndNameAndMiddleNameAndBirthday(participantDTO.getLastname(),participantDTO.getName(),participantDTO.getMiddleName(),participantDTO.getBirthday()).defaultIfEmpty(new Participant());
        }else{
            return participantRepository.findByLastnameAndNameAndBirthday(participantDTO.getLastname(),participantDTO.getName(),participantDTO.getBirthday()).defaultIfEmpty(new Participant());
        }
    }
    //@Cacheable("participants")
    public Mono<Participant> getById(int participantId) {
        return participantRepository.findById(participantId).defaultIfEmpty(new Participant());
    }
    // FIND FLUX
    //@Cacheable("participants")
    public Flux<Participant> findAllByLastnameLike(String query) {
        return participantRepository.findParticipantsWithPartOfLastname("%" + query + "%").collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
    //@Cacheable("participants")
    public Flux<Participant> getAllFreeParticipants(){
        return participantRepository.findAllFreeParticipants().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
    //@Cacheable("participants")
    public Flux<Participant> getAll(){
        return participantRepository.findAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Participant::getFullName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
    //@Cacheable("participants")
    public Flux<Participant> findByIds(Set<Integer> ids) {
        return participantRepository.findAllByIdIn(ids);
    }

    //@Cacheable("participants")
    public Flux<Participant> findAllByLastname(String lastname) {
        return participantRepository.findAllByLastname(lastname);
    }

    //@Cacheable("participants")
    public Flux<Participant> getAllByFirstLetter(String letter) {
        return participantRepository.findAllWhereFirstLetterIs(letter);
    }

    //@Cacheable(value = "participants", key = "#search")
    public Mono<Participant> findByFullName(String search) {
        System.out.println(search);
        String[] parts = search.split(" ");
        if (parts.length == 3) {
            return participantRepository.findAllByLastnameAndNameAndMiddleName(parts[0], parts[1], parts[2])
                    .filter(participant -> participant.getFullName().equals(search))
                    .next()
                    .switchIfEmpty(Mono.just(new Participant()));
        } else if (parts.length == 2) {
            return participantRepository.findAllByLastnameAndName(parts[0], parts[1])
                    .filter(participant -> participant.getFullName().equals(search))
                    .next()
                    .switchIfEmpty(Mono.just(new Participant()));
        } else {
            return Mono.just(new Participant());
        }
    }

    public Flux<Participant> findAllBySearchQuery(String query){
        String[] parts = query.split(" ");
        Flux<Participant> participantFlux = Flux.empty();
        for(String part : parts){
            part = part.trim();
            Flux<Participant> byLastname = participantRepository.findAllByLastnameLikeIgnoreCase(part);
            Flux<Participant> byName = participantRepository.findAllByNameLikeIgnoreCase(part);
            Flux<Participant> byMiddleName = participantRepository.findAllByMiddleNameLikeIgnoreCase(part);
            participantFlux = participantFlux.mergeWith(byLastname).mergeWith(byName).mergeWith(byMiddleName);
        }

        participantFlux = participantFlux.distinct(Participant::getId);
        participantFlux = participantFlux.filter(participant -> query.equalsIgnoreCase(participant.getFullName()));
        return participantFlux;
    }

    // CREATE
    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> addQualificationToParticipant(Qualification qualification) {
        return participantRepository.findById(qualification.getParticipantId()).flatMap(participant -> {
            participant.addQualification(qualification);
            return participantRepository.save(participant);
        });
    }
    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> saveParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> addNewParticipant(ParticipantDTO participantDTO) {
        return Mono.just(new Participant(participantDTO)).flatMap(participantRepository::save);
    }

    // UPDATE
    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> removeSchoolFromParticipant(int pid, int sid) {
        return participantRepository.findById(pid).flatMap(participant -> {
            participant.getSportSchoolIds().remove(sid);
            return participantRepository.save(participant);
        });
    }
    //@CacheEvict(value = "participants", allEntries = true)
    public Flux<Participant> removeSchoolFromParticipants(SportSchool sportSchool) {
        return participantRepository.findAllByIdIn(sportSchool.getParticipantIds()).flatMap(participant -> {
            participant.getSportSchoolIds().remove(sportSchool.getId());
            return participantRepository.save(participant);
        }).defaultIfEmpty(new Participant());
    }

    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> updateSchool(int id, int oldSchoolId, int schoolId) {
        return participantRepository.findById(id).flatMap(participant -> {
            participant.getSportSchoolIds().remove(oldSchoolId);
            participant.addSportSchoolId(schoolId);
            return participantRepository.save(participant);
        });
    }

    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> updateParticipantData(ParticipantDTO participantDTO) {
        return participantRepository.findById(participantDTO.getId()).flatMap(participant -> {
            participant.setLastname(participantDTO.getLastname());
            participant.setName(participantDTO.getName());
            participant.setMiddleName(participantDTO.getMiddleName());
            participant.setBirthday(participantDTO.getBirthday());
            return participantRepository.save(participant);
        });
    }

    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> updateSchool(int id, int schoolId) {
        return participantRepository.findById(id).flatMap(participant -> {
            participant.addSportSchoolId(schoolId);
            return participantRepository.save(participant);
        });
    }

    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> removeQualificationFromParticipant(Qualification qualification) {
        return participantRepository.findById(qualification.getParticipantId()).flatMap(participant -> {
            participant.getQualificationIds().remove(qualification.getId());
            return participantRepository.save(participant);
        });
    }

    // DELETE

    //@CacheEvict(value = "participants", allEntries = true)
    public Mono<Participant> deleteParticipant(int pid) {
        return participantRepository.findById(pid).flatMap(participant -> participantRepository.delete(participant).then(Mono.just(participant)));
    }

    // COUNT
    public Mono<Long> getCount() {
        return participantRepository.count();
    }

    public Flux<Participant> getAllByIdIn(Set<Integer> pidList) {
        return participantRepository.findAllByIdIn(pidList);
    }

    public Flux<Participant> getAllByIdInAndSearchQuery(Set<Integer> pidList, String search) {
        String[] searchParts = search.split(" ");
        List<Flux<Participant>> fluxes = new ArrayList<>();
        for(String part : searchParts){
            if (!part.equals("")) {
                String searchData = "%" + part + "%";
                Flux<Participant> lastnameFlux = participantRepository.findAllByIdInAndLastnameLikeIgnoreCase(pidList, searchData).switchIfEmpty(Flux.empty());
                Flux<Participant> nameFlux = participantRepository.findAllByIdInAndNameLikeIgnoreCase(pidList, searchData).switchIfEmpty(Flux.empty());
                Flux<Participant> middleNameFlux = participantRepository.findAllByIdInAndMiddleNameLikeIgnoreCase(pidList, searchData).switchIfEmpty(Flux.empty());
                fluxes.add(lastnameFlux);
                fluxes.add(nameFlux);
                fluxes.add(middleNameFlux);
            }
        }
        return filterParticipants(fluxes,searchParts);
    }

    public Flux<Participant> getAllBySearchQuery(String search) {
        String[] searchParts = search.split(" ");
        List<Flux<Participant>> fluxes = new ArrayList<>();
        for(String part : searchParts){
            if (!part.equals("")) {
                String searchData = "%" + part + "%";
                Flux<Participant> lastnameFlux = participantRepository.findAllByLastnameLikeIgnoreCase(searchData).switchIfEmpty(Flux.empty());
                Flux<Participant> nameFlux = participantRepository.findAllByNameLikeIgnoreCase(searchData).switchIfEmpty(Flux.empty());
                Flux<Participant> middleNameFlux = participantRepository.findAllByMiddleNameLikeIgnoreCase(searchData).switchIfEmpty(Flux.empty());
                fluxes.add(lastnameFlux);
                fluxes.add(nameFlux);
                fluxes.add(middleNameFlux);
            }
        }

        return filterParticipants(fluxes,searchParts);
    }

    private Flux<Participant> filterParticipants(List<Flux<Participant>> fluxes, String[] searchParts){
        return Flux.merge(fluxes).distinct().flatMap(participant -> {
            int check = 0;
            for(String part : searchParts){
                if (participant.getLastname().toLowerCase().contains(part.toLowerCase())){
                    check++;
                }
                if (participant.getName().toLowerCase().contains(part.toLowerCase())){
                    check++;
                }
                if (participant.getMiddleName().toLowerCase().contains(part.toLowerCase())){
                    check++;
                }
            }
            if(check >= searchParts.length){
                return Mono.just(participant);
            }else{
                return Mono.empty();
            }
        });
    }
}
