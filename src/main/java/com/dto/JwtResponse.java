package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String twoFactorToken;

    public JwtResponse(String token, String type, String username, String email) {
        this.token = token;
        this.type = type;
        this.username = username;
        this.email = email;
    }
}
