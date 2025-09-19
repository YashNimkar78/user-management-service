package com.service.usermanagement.repository;



import com.service.usermanagement.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional  
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
      
        testRole = new Role();
        testRole.setName("ROLE_USER");
        roleRepository.save(testRole);
    }

   
    // Positive test findById
    
    @Test
    void findById_existingRole_returnsRole() {
        Optional<Role> roleOpt = roleRepository.findById("ROLE_USER");

        assertThat(roleOpt).isPresent();
        assertThat(roleOpt.get().getName()).isEqualTo("ROLE_USER");
    }

    

    
    // Positive test save role
    
    @Test
    void save_newRole_savesSuccessfully() {
        Role newRole = new Role();
        newRole.setName("ROLE_ADMIN");

        Role savedRole = roleRepository.save(newRole);

        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getName()).isEqualTo("ROLE_ADMIN");
    }

    
    // Negative test delete role
  
    @Test
    void delete_existingRole_removesRole() {
        roleRepository.delete(testRole);

        Optional<Role> roleOpt = roleRepository.findById("ROLE_USER");
        assertThat(roleOpt).isEmpty();
    }
}
