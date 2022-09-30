package com.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.jwt.JwtCookieFilter;
import com.service.PersonAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    public static class JwtSecurityConfig extends WebSecurityConfigurerAdapter {
        private static final String[] JWT_AUTHENTICATION_REGEX_MATCHERS = {
                "/home",
                "/verification"
        };
        private final PersonAuthenticationProvider authenticationProvider;
        private final ObjectMapper objectMapper;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.formLogin().disable().logout().disable().httpBasic().disable().csrf().disable();

            http.requestMatchers(configurer -> configurer.regexMatchers(JWT_AUTHENTICATION_REGEX_MATCHERS))
                    .sessionManagement()
                    .sessionCreationPolicy(STATELESS)
                    .and()
                    .addFilterAfter(new JwtCookieFilter(objectMapper), BasicAuthenticationFilter.class)
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
        }
    }

    @Configuration
    public static class AnonymousSecurityConfig extends WebSecurityConfigurerAdapter {
        private static final String AUTHENTICATION_NOT_REQUIRED_ANT_MATCHER = "/**";

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.formLogin().disable().logout().disable().httpBasic().disable().csrf().disable();

            http.antMatcher(AUTHENTICATION_NOT_REQUIRED_ANT_MATCHER)
                    .sessionManagement()
                    .sessionCreationPolicy(STATELESS)
                    .and()
                    .authorizeRequests()
                    .anyRequest()
                    .permitAll();
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

}
