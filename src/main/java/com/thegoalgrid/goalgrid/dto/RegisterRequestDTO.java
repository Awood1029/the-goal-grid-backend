// RegisterRequest.java
package com.thegoalgrid.goalgrid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Username is mandatory")
    private String firstName;

    @NotBlank(message = "Username is mandatory")
    private String lastName;
}
