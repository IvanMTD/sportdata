package ru.fcpsr.sportdata.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.dto.PasswordDTO;
import ru.fcpsr.sportdata.dto.UserDTO;
import ru.fcpsr.sportdata.models.SysUser;
import ru.fcpsr.sportdata.repositories.UserRepository;

@Data
@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // FIND ALL
    public Flux<SysUser> getAll() {
        return userRepository.findAll();
    }
    // FIND ONE
    public Mono<SysUser> getById(int id) {
        return userRepository.findById(id);
    }
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).flatMap(Mono::just);
    }
    // SAVE
    public Mono<SysUser> saveUser(UserDTO user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(new SysUser(user));
    }

    public Mono<SysUser> save(SysUser user){
        return userRepository.save(user);
    }

    public Mono<SysUser> save(UserDTO dto){
        dto.setPassword(encoder.encode(dto.getPassword()));
        SysUser user = new SysUser(dto);
        return userRepository.save(user);
    }
    // UPDATE
    public Mono<SysUser> updateUserData(UserDTO user) {
        return userRepository.findById(user.getId()).flatMap(u -> {
            u.setUsername(user.getUsername());
            u.setEmail(user.getEmail());
            u.setRole(user.getRole());
            return userRepository.save(u);
        });
    }

    public Mono<SysUser> updateUserPassword(int userId, PasswordDTO passwordDTO) {
        return userRepository.findById(userId).flatMap(user -> {
            user.setPassword(encoder.encode(passwordDTO.getNewPassword()));
            return userRepository.save(user);
        });
    }
    // DELETE
    public Mono<SysUser> deleteUser(UserDTO dto){
        return userRepository.findById(dto.getId()).flatMap(user -> userRepository.delete(user).then(Mono.just(user)));
    }
}
