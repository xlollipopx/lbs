package com.service;

import com.model.Person;
import com.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonService {
    private final PersonRepository personRepository;
    private PasswordEncoder passwordEncoder;

    public void savePerson(Person person) {
        person.setEncodedPassword(passwordEncoder.encode(person.getEncodedPassword()));
        personRepository.save(person);
    }

    public Person getPersonByEmail(String email) {
        return personRepository.getPersonByEmail(email).get();
    }

//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Person person = getPersonByEmail(email);
//        String mail = "";
//        String password = "";
//        if(person != null) {
//            mail = person.getEmail();
//            password = person.getEncodedPassword();
//         //   throw new UsernameNotFoundException("User not found");
//
//        }
//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("user"));
//        return new org.springframework.security.core.userdetails.User(mail, password, authorities);
//    }
}
