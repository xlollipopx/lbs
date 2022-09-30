package com.dto;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Getter
public class AuthenticatedPerson extends AbstractAuthenticationToken {

    private final String accessToken;
    private final String email;

    public AuthenticatedPerson(String accessToken) {
        super(Collections.emptyList());
        this.accessToken = accessToken;
        this.email = null;
    }

    public AuthenticatedPerson(String accessToken, String email) {
        super(Collections.emptyList());
        this.accessToken = accessToken;
        this.email = email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }
}
