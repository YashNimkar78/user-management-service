package com.service.usermanagement.model;



import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String role; // optional - "ROLE_ADMIN" or "ROLE_USER"
}
