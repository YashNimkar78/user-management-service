package com.service.usermanagement.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class Role {
    @Id
    private String name; 
}
