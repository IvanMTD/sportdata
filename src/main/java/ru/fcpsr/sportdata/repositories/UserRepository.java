package ru.fcpsr.sportdata.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.models.SysUser;

public interface UserRepository extends ReactiveCrudRepository<SysUser,Integer> {
    Mono<SysUser> findByUsername(String username);
}
