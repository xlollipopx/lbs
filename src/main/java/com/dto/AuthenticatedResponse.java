package com.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedResponse {

    private String username;
    private String email;
    private String info;
}
