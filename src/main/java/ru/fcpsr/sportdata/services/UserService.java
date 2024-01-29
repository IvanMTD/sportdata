package ru.fcpsr.sportdata.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.UserDTO;
import ru.fcpsr.sportdata.models.SysUser;
import ru.fcpsr.sportdata.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // FIND ALL
    // FIND ONE
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).flatMap(Mono::just);
    }
    // SAVE
    public Mono<SysUser> saveUser(UserDTO user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(new SysUser(user));
    }
    // UPDATE
    // DELETE
}
