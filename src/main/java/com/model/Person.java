package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person {

    @Id
    @Column(name = "person_id", nullable = false, updatable = false)
    @JsonProperty("person_id")
    private Long person_id;

    @Column(name = "username")
    @JsonProperty("username")
    private String username;

    @Column(name = "email")
    @JsonProperty("email")
    private String email;

    @Column(name = "password")
    @JsonProperty("password")
    private String encodedPassword;

    public Person(String username, String email, String encodedPassword) {
        this.person_id = 1L;
        this.username = username;
        this.email = email;
        this.encodedPassword = encodedPassword;
    }
}
