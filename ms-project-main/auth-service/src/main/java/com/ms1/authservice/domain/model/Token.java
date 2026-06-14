package com.ms1.authservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // Constructor vacío
@AllArgsConstructor
public class Token {
    private String accessToken;
    private String tokenType; // "Bearer"
    private Long expiresIn;   // Tiempo de vida

}