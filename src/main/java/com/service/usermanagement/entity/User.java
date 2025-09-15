package com.service.usermanagement.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_roles",
        joinColumns = @JoinColumn(name="user_id"),
        inverseJoinColumns = @JoinColumn(name="role_name"))
    private Set<Role> roles;
}
