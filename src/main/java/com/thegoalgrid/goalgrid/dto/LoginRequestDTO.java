package com.thegoalgrid.goalgrid.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "Name is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
