package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.Place;
import ru.fcpsr.sportdata.repositories.PlaceRepository;

import java.util.ArrayList;
import java.util.List;
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
    @CachePut("places")
    public Mono<Place> setPlace(Place place) {
        return placeRepository.save(place);
    }

    @Cacheable("places")
    public Flux<Place> getAllByIdIn(Set<Integer> placeIds) {
        return placeRepository.findAllByIdIn(placeIds).defaultIfEmpty(new Place());
    }

    @Cacheable("places")
    public Flux<Place> getAllByIdInTotal(List<Integer> placesIds) {
        return placeRepository.findAllByIdIn(placesIds).switchIfEmpty(
                Flux.fromIterable(placesIds).collectList().flatMapMany(l -> {
                    List<Place> places = new ArrayList<>();
                    for(int i=0; i<l.size(); i++){
                        places.add(new Place());
                    }
                    return Flux.fromIterable(places);
                })
        );
    }

    @Cacheable("places")
    public Mono<Place> getById(int id) {
        return placeRepository.findById(id).defaultIfEmpty(new Place());
    }

    @CacheEvict("places")
    public Mono<Place> deleteById(int placeId) {
        return placeRepository.findById(placeId).flatMap(place -> placeRepository.delete(place).then(Mono.just(place)));
    }
}
