package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonDetailsServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonDetailsService personDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        Person person = new Person();
        person.setLogin("user");

        when(personRepository.findByLogin("user")).thenReturn(Optional.of(person));

        UserDetails details = personDetailsService.loadUserByUsername("user");

        assertNotNull(details);
        assertTrue(details instanceof PersonDetails);
        assertEquals("user", details.getUsername());
        verify(personRepository).findByLogin("user");
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void loadUserByUsername_whenUserMissing_throwsUsernameNotFound() {
        when(personRepository.findByLogin("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> personDetailsService.loadUserByUsername("missing"));

        verify(personRepository).findByLogin("missing");
        verifyNoMoreInteractions(personRepository);
    }
}
