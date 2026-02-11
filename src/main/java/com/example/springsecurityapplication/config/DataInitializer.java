package com.example.springsecurityapplication.config;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(PersonRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.count() == 0) {
                Person user = new Person();
                user.setLogin("user1");
                user.setPassword(encoder.encode("user1"));
                user.setRole("ROLE_USER");
                repo.save(user);

                Person admin = new Person();
                admin.setLogin("admin1");
                admin.setPassword(encoder.encode("admin1"));
                admin.setRole("ROLE_ADMIN");
                repo.save(admin);
            }
        };
    }
}

