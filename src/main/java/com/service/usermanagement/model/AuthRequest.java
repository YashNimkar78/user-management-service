package com.service.usermanagement.model;



import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthRequest {
    private String email;
    private String password;
}
