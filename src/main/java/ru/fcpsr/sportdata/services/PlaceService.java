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
import ru.fcpsr.sportdata.dto.ParticipantContestDTO;
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
    //@CacheEvict(value = "places", allEntries = true)
    public Mono<Place> setPlace(Place place) {
        return placeRepository.save(place);
    }

    //@Cacheable("places")
    public Flux<Place> getAllByIdIn(Set<Long> placeIds) {
        return placeRepository.findAllByIdIn(placeIds).defaultIfEmpty(new Place());
    }

    //@Cacheable("places")
    public Flux<Place> getAllByIdInTotal(List<Long> placesIds) {
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

    //@Cacheable("places")
    public Mono<Place> getById(long id) {
        return placeRepository.findById(id).defaultIfEmpty(new Place());
    }

    //@CacheEvict(value = "places", allEntries = true)
    public Mono<Place> deleteById(int placeId) {
        return placeRepository.findById(placeId).flatMap(place -> placeRepository.delete(place).then(Mono.just(place)));
    }

    public Flux<Place> deleteAllCurrent(Set<Long> placeIds) {
        return placeRepository
                .findAllByIdIn(placeIds)
                .collectList()
                .flatMap(list -> placeRepository.deleteAll(list).then(Mono.just(list)))
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(Flux.fromIterable(new ArrayList<>()));
    }

    public Flux<Place> getAllWhereParticipantIdIs(int participantId) {
        return placeRepository.findAllByParticipantId(participantId);
    }
}
