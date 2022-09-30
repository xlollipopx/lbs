package com.security.jwt;

import com.controller.exception.AuthenticationException;
import com.dto.AuthenticatedPerson;
import com.dto.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtCookieFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE = "access_token";

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            SecurityContextHolder.clearContext();
            String accessToken = getCookieValue(request.getCookies(), ACCESS_TOKEN_COOKIE)
                    .orElseThrow(() -> new AuthenticationException(
                            "Access token cookie is missing"));

            SecurityContextHolder.getContext()
                    .setAuthentication(new AuthenticatedPerson(accessToken));

            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            log.warn(e.getMessage());
            setErrorResponse(response, e, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Cannot handle authentication", e);
            setErrorResponse(response, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void setErrorResponse(HttpServletResponse response, Exception e, HttpStatus httpStatus)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus.value());

        PrintWriter responseOut = response.getWriter();
        responseOut.print(objectMapper.writeValueAsString(ErrorDto.builder()
                .error(ErrorDto.Error.builder()
                        .msg(e.getMessage())
                        .build())
                .build()));
        responseOut.flush();
    }

    private Optional<String> getCookieValue(Cookie[] cookies, String cookieKey) {
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookieKey.equals(cookie.getName().toLowerCase()))
                .findFirst()
                .map(Cookie::getValue);
    }

}