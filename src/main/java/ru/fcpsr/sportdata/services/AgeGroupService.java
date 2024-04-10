package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.AgeGroupDTO;
import ru.fcpsr.sportdata.models.AgeGroup;
import ru.fcpsr.sportdata.repositories.AgeGroupRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgeGroupService {
    private final AgeGroupRepository groupRepository;
    // FIND MONO
    @Cacheable("groups")
    public Mono<AgeGroup> getById(int ageGroupId) {
        return groupRepository.findById(ageGroupId).defaultIfEmpty(new AgeGroup());
    }
    // FIND FLUX
    @Cacheable("groups")
    public Flux<AgeGroup> getAllByIdsList(List<Integer> ageGroupIds) {
        return groupRepository.findAllByIdIn(ageGroupIds);
    }
    @Cacheable("groups")
    public Flux<AgeGroup> getAllByIds(Set<Integer> ageGroupIds) {
        return groupRepository.findAllById(ageGroupIds);
    }
    @Cacheable("groups")
    public Flux<AgeGroup> getAllByIds2(Set<Integer> ageGroupIds) {
        return groupRepository.findAllById(ageGroupIds).defaultIfEmpty(new AgeGroup());
    }
    @Cacheable("groups")
    public Flux<AgeGroup> getAll() {
        return groupRepository.findAll();
    }
    // CREATE
    @CacheEvict(value = "groups", allEntries = true)
    public Mono<AgeGroup> addNewGroup(AgeGroupDTO groupDTO) {
        return Mono.just(new AgeGroup()).flatMap(group -> {
            group.setTitle(groupDTO.getTitle());
            group.setMinAge(groupDTO.getMinAge());
            group.setMaxAge(groupDTO.getMaxAge());
            group.setTypeOfSportId(groupDTO.getSportId());
            return groupRepository.save(group);
        });
    }
    // UPDATE
    @CachePut(value = "groups", key="#groupDTO.id")
    public Mono<AgeGroup> updateTitle(AgeGroupDTO groupDTO) {
        return groupRepository.findById(groupDTO.getId()).flatMap(group -> {
            group.setTitle(groupDTO.getTitle());
            return groupRepository.save(group);
        });
    }
    @CachePut(value = "groups", key="#groupDTO.id")
    public Mono<AgeGroup> updateAges(AgeGroupDTO groupDTO) {
        return groupRepository.findById(groupDTO.getId()).flatMap(group -> {
            group.setMinAge(groupDTO.getMinAge());
            group.setMaxAge(groupDTO.getMaxAge());
            return groupRepository.save(group);
        });
    }
    // DELETE
    @CacheEvict(value = "groups", allEntries = true)
    public Mono<AgeGroup> deleteGroup(int groupId) {
        return groupRepository.findById(groupId).flatMap(group -> groupRepository.deleteById(group.getId()).then(Mono.just(group)));
    }
    public Mono<Long> getCount() {
        return groupRepository.count();
    }
    public Flux<AgeGroup> getAllBySportId(int sportId) {
        return groupRepository.findAllByTypeOfSportId(sportId).collectList().flatMapMany(gl -> {
            gl = gl.stream().sorted(Comparator.comparing(AgeGroup::getId)).collect(Collectors.toList());
            return Flux.fromIterable(gl);
        }).flatMapSequential(Mono::just);
    }
}
