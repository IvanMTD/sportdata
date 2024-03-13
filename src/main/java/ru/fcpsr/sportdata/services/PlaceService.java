package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.repositories.PlaceRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    /**
     * CREATE
     * @param place
     * @return
     */
    public Mono<Place> addPlace(Place place) {
        return placeRepository.save(place);
    }

    public Flux<Place> getAllByIdIn(Set<Integer> placeIds) {
        return placeRepository.findAllByIdIn(placeIds);
    }
}
