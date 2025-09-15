package com.service.usermanagement;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.service.usermanagement.entity.Role;
import com.service.usermanagement.entity.User;
import com.service.usermanagement.repository.RoleRepository;
import com.service.usermanagement.repository.UserRepository;

@SpringBootApplication
public class UsermanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsermanagementApplication.class, args);
	}
	@Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            if(!roleRepo.existsById("ROLE_USER")) roleRepo.save(new Role("ROLE_USER"));
            if(!roleRepo.existsById("ROLE_ADMIN")) roleRepo.save(new Role("ROLE_ADMIN"));

            if(!userRepo.findByEmail("admin@example.com").isPresent()){
                User admin = new User();
                admin.setEmail("admin@example.com");
                admin.setFullName("Admin User");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRoles(Set.of(roleRepo.findById("ROLE_ADMIN").get()));
                userRepo.save(admin);
            }
        };
    }
}
