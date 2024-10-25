package ru.fcpsr.sportdata.configurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Mono;
import ru.fcpsr.sportdata.enums.Role;
import ru.fcpsr.sportdata.models.SysUser;
import ru.fcpsr.sportdata.repositories.UserRepository;
import ru.fcpsr.sportdata.util.NamingUtil;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    @Value("${app.admin}")
    private String username;
    @Value("${app.admin.pass}")
    private String password;

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName(NamingUtil.getInstance().getSessionName());
        //resolver.setCookieMaxAge(Duration.ofHours(24));
        return resolver;
    }

    @Bean
    public CommandLineRunner prepare(UserRepository userRepository, PasswordEncoder encoder){
        return args -> {
            log.info("***************** environments start *******************");
            for(String key : System.getenv().keySet()){
                String[] p = key.split("\\.");
                if(p.length > 1) {
                    log.info("\"{}\"=\"{}\"", key, System.getenv().get(key));
                }
            }
            log.info("****************** environments end ********************");
            userRepository.findByUsername("admin").flatMap(existingUser -> {
                log.info("User {} exist.", existingUser.getUsername());
                return Mono.just(existingUser);
            }).switchIfEmpty(
                    Mono.defer(() -> {
                        log.info("User not found! Create default admin user: {}, password: {}", username,password);
                        SysUser user = new SysUser();
                        user.setUsername(username);
                        user.setPassword(encoder.encode(password));
                        user.setEmail("karachkov_is@fcpsr.ru");
                        user.setRole(Role.ADMIN);
                        return userRepository.save(user);
                    })
            ).flatMap(u -> {
                log.info("User in db: [{}]", u);
                return Mono.empty();
            }).subscribe();
        };
    }
}
