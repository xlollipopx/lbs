package com.service;

import com.dto.AuthenticatedPerson;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonAuthenticationProvider implements AuthenticationProvider {

    private final PersonAuthenticationService personAuthService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticatedPerson authPerson = (AuthenticatedPerson) authentication;
        return personAuthService.authenticate(authPerson.getAccessToken());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticatedPerson.class);
    }

}