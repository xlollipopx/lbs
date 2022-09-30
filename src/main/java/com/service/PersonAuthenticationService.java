package com.service;


import com.controller.exception.AuthenticationException;
import com.dto.AuthenticatedPerson;
import com.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonAuthenticationService {

    private final JwtUtils jwtUtils;


    public AuthenticatedPerson authenticate(String accessToken) {
        String email;
        try {
            if(jwtUtils.validateJwtToken(accessToken)) {
                email = jwtUtils.getEmailFromJwtToken(accessToken);
            } else{
                throw new AuthenticationException();
            }


            return new AuthenticatedPerson(accessToken, email);
        } catch (Exception e) {
            throw new AuthenticationException();
        }

    }

    public AuthenticatedPerson getAuthenticatedPerson() {
        return (AuthenticatedPerson) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new AuthenticationException("No authentication available in security context"));
    }

    public enum Roles {
        USER
    }
}
