package com.service.usermanagement.repository;


import com.service.usermanagement.entity.User;
import com.service.usermanagement.entity.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional  
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        
        Role role = new Role();
        role.setName("ROLE_USER");

       
        testUser = new User();
        testUser.setEmail("user1@example.com");
        testUser.setPassword("password123");
        testUser.setRoles(Set.of(role));

        // save to the database
        userRepository.save(testUser);
    }

    // Positive test findByEmail
   
    @Test
    void findByEmail_existingEmail_returnsUser() {
        Optional<User> userOpt = userRepository.findByEmail("user1@example.com");

        assertThat(userOpt).isPresent();
        assertThat(userOpt.get().getEmail()).isEqualTo("user1@example.com");
    }

    
    // Negative test findByEmail
    
    @Test
    void findByEmail_nonExistingEmail_returnsEmpty() {
        Optional<User> userOpt = userRepository.findByEmail("missing@example.com");

        assertThat(userOpt).isEmpty();
    }

    // Positive test existsByEmail
    
    @Test
    void existsByEmail_existingEmail_returnsTrue() {
        boolean exists = userRepository.existsByEmail("user1@example.com");
        assertThat(exists).isTrue();
    }

    
    // Negative test existsByEmail
   
    @Test
    void existsByEmail_nonExistingEmail_returnsFalse() {
        boolean exists = userRepository.existsByEmail("missing@example.com");
        assertThat(exists).isFalse();
    }
}

