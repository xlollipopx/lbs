package com.controller;


import com.dto.*;
import com.model.Person;
import com.repository.PersonRepository;
import com.security.jwt.JwtUtils;
import com.service.PersonAuthenticationService;
import com.utils.Cypher;
import com.utils.EmailNotificator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class HomeController {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";
    private final HashMap<String, SignupDto> pendingSignupPersonMap = new HashMap<>();
    private final HashMap<String, LoginDto> pendingLoginPersonMap = new HashMap<>();

    private final EmailNotificator emailNotificator;
    private final JwtUtils jwtUtils;
    private final PersonAuthenticationService personAuthenticationService;
    private final PersonRepository personRepository;
    private final  AuthenticationManager authenticationManager;
    @Value("${twofactor.app.passwordKey}")
    private String passwordKey;

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginDto dto) {
        Optional<Person> personOpt = personRepository.getPersonByEmail(dto.getEmail());
        if(!personOpt.isPresent()) {
            return buildErrorResponse("User doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        Person person = personOpt.get();

        String actualPassword = null;
        try {
            actualPassword = Cypher.decrypt(person.getEncodedPassword(), passwordKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(!actualPassword.equals(dto.getPassword())) {
            return buildErrorResponse("Email or password are incorrect.", HttpStatus.BAD_REQUEST);
        }

        String verificationCode = emailNotificator.generateVerificationCode();
        try {
            emailNotificator.send(dto.getEmail(), verificationCode);
        } catch (IOException | MessagingException e) {
            log.info("unable to send email", e);
            return buildErrorResponse("Invalid email. Unable to send message.", HttpStatus.BAD_REQUEST);
        }

        String tmpToken = Cypher.generateRandomSecret();
        dto.setTwoFactorToken(tmpToken);
        dto.setVerificationCode(verificationCode);
        pendingLoginPersonMap.put(dto.getEmail(), dto);

        JwtResponse jwtResponse =
                new JwtResponse(null,"Non", person.getUsername(), person.getEmail(), tmpToken);
        return buildResponse(jwtResponse, HttpStatus.OK);
    }

    @GetMapping("/2factor")
    ResponseEntity<?> twoFactor(@RequestBody TwoFactorCodeDto code) {

        LoginDto loginDto = pendingLoginPersonMap.get(code.getEmail());

        if(!code.getCode().equals(loginDto.getVerificationCode()) ||
        !loginDto.getTwoFactorToken().equals(code.getTwoFactorToken())) {
            log.info(loginDto.getVerificationCode());
            log.info(loginDto.getTwoFactorToken());
            return buildErrorResponse("Verification code doesn't match!", HttpStatus.BAD_REQUEST);
        }
        pendingLoginPersonMap.remove(code.getEmail());

        String token = jwtUtils.generateJwtToken(code.getEmail());
        JwtResponse jwtResponse = new JwtResponse(token,"Bearer", null, code.getEmail());

        return buildResponse(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignupDto request) {

        if(personRepository.getPersonByEmail(request.getEmail()).isPresent()) {
            return buildErrorResponse("Email is already in use.", HttpStatus.BAD_REQUEST);
        }

        String verificationCode = emailNotificator.generateVerificationCode();
        try {
            emailNotificator.send(request.getEmail(), verificationCode);
        } catch (IOException | MessagingException e) {
            log.info("unable to send email", e);
            return buildErrorResponse("Invalid email. Unable to send message.", HttpStatus.BAD_REQUEST);
        }
        request.setVerificationCode(verificationCode);
        pendingSignupPersonMap.put(request.getEmail(), request);

        String token = jwtUtils.generateJwtToken(request.getEmail());
        JwtResponse jwtResponse = new JwtResponse(token,"Bearer", request.getUsername(), request.getEmail());
        log.info("Person: {}", jwtResponse);
        return buildResponse(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/verification")
    ResponseEntity<?> verification(@RequestBody VerificationCodeDto code) {

        String email = personAuthenticationService.getAuthenticatedPerson().getEmail();
        if(personRepository.getPersonByEmail(email).isPresent()) {
            return buildErrorResponse("User already exists.", HttpStatus.BAD_REQUEST);
        }
        SignupDto signupDto = pendingSignupPersonMap.get(email);

        if(!code.getCode().equals(signupDto.getVerificationCode())) {
            return buildErrorResponse("Verification code doesn't match!", HttpStatus.BAD_REQUEST);
        }

        Person person = null;
        try {
            person = new Person(signupDto.getUsername(),
                    signupDto.getEmail(),
                    Cypher.encrypt(signupDto.getPassword(), passwordKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        personRepository.save(person);

        AuthenticatedResponse res = new AuthenticatedResponse(person.getUsername(), person.getEmail(), null);
        return buildResponse(res, HttpStatus.OK);
    }

    @GetMapping("/home")
    ResponseEntity<?> home() {
        Authentication authentication;

        return ResponseEntity.status(HttpStatus.OK)
                .body("Helloo");
    }


    private <T> ResponseEntity<ResponseDto<T>> buildResponse(T data, HttpStatus httpStatus) {
        var response = new ResponseDto<>(data);
        return ResponseEntity.status(httpStatus).body(response);
    }

    private <T> ResponseEntity<ResponseDto<ErrorDto.Error>> buildErrorResponse(String errorMessage, HttpStatus httpStatus) {
        ResponseEntity<ResponseDto<ErrorDto.Error>> response = buildResponse(null, httpStatus);
        response.getBody().setError(new ErrorDto.Error(errorMessage));
        return response;

    }



    public static String findCookieValue(Cookie[] cookies, String cookieKey) {
        return Arrays.stream(Optional.ofNullable(cookies).orElse(new Cookie[] {}))
                .filter(cookie -> cookie.getName().equals(cookieKey))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
