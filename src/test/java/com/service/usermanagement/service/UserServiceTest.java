package com.service.usermanagement.service;

import com.service.usermanagement.entity.Role;
import com.service.usermanagement.entity.User;
import com.service.usermanagement.exceptionhandler.ResourceNotFoundException;
import com.service.usermanagement.model.RegisterRequest;
import com.service.usermanagement.repository.RoleRepository;
import com.service.usermanagement.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest request;
    private Role userRole;
    private User savedUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setFullName("Test User");

        userRole = new Role();
        userRole.setName("ROLE_USER");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setFullName("Test User");
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(userRole));
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findById("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(request);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getRoles()).hasSize(1);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_RoleNotFound() {
        request.setRole("ROLE_ADMIN");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findById("ROLE_ADMIN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.register(request));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(savedUser));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("test@example.com");
    }
    
    //negative method for getAllUser.
    @Test
    void testGetAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> users = userService.getAllUsers();
        assertThat(users).isEmpty();

        verify(userRepository, times(1)).findAll();
    }


    @Test
    void testGetUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        User result = userService.getUser(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(1L);
    }
}
