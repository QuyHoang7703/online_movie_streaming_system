package com.example.OnlineMovieStreamingSystem.config;

import com.example.OnlineMovieStreamingSystem.domain.user.Role;
import com.example.OnlineMovieStreamingSystem.domain.user.User;
import com.example.OnlineMovieStreamingSystem.domain.user.UserDetail;
import com.example.OnlineMovieStreamingSystem.repository.RoleRepository;
import com.example.OnlineMovieStreamingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        long countRole = this.roleRepository.count();
        long countUser = this.userRepository.count();
        log.info("Database initialization started");
        if(countRole == 0) {
            List<Role> roles = new ArrayList<>();
            roles.add(new Role("ADMIN"));
            roles.add(new Role("USER"));
            this.roleRepository.saveAll(roles);
        }

        if(countUser == 0) {
            Role adminRole = this.roleRepository.findByName("ADMIN");
            User user = User.builder()
                    .email("admin@gmail.com")
                    .password(this.passwordEncoder.encode("1234567"))
                    .active(true)
                    .role(adminRole)
                    .build();
            UserDetail userDetails = UserDetail.builder()
                    .name("Admin")
                    .phoneNumber("0908761112")
                    .avatarUrl("abc")
                    .user(user)
                    .build();
            user.setUserDetail(userDetails);

            this.userRepository.save(user);
        }

        log.info("Database initialization completed");

    }
}
