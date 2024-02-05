package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.fcpsr.sportdata.models.AgeGroup;
import ru.fcpsr.sportdata.repositories.AgeGroupRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AgeGroupService {
    private final AgeGroupRepository groupRepository;

    public Flux<AgeGroup> getAllByIdsList(List<Integer> ageGroupIds) {
        return groupRepository.findAllByIdIn(ageGroupIds);
    }
}
