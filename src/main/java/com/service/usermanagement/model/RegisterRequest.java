package com.service.usermanagement.model;



import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
	private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
	private String password;
    @NotBlank(message = "Full name is mandatory")
	private String fullName;
	private String role; 
}
