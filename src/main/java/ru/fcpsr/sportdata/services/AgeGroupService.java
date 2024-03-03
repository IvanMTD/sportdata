package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.AgeGroupDTO;
import ru.fcpsr.sportdata.models.AgeGroup;
import ru.fcpsr.sportdata.repositories.AgeGroupRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AgeGroupService {
    private final AgeGroupRepository groupRepository;

    // FIND MONO

    // FIND FLUX
    public Flux<AgeGroup> getAllByIdsList(List<Integer> ageGroupIds) {
        return groupRepository.findAllByIdIn(ageGroupIds);
    }

    public Flux<AgeGroup> getAll() {
        return groupRepository.findAll();
    }

    // CREATE
    public Mono<AgeGroup> addNewGroup(AgeGroupDTO groupDTO) {
        return Mono.just(new AgeGroup()).flatMap(group -> {
            group.setDisciplineId(groupDTO.getDisciplineId());
            group.setTitle(groupDTO.getTitle());
            return groupRepository.save(group);
        });
    }
}
