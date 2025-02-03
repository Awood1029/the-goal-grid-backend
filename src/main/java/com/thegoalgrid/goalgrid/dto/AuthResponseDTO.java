package com.thegoalgrid.goalgrid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
}
