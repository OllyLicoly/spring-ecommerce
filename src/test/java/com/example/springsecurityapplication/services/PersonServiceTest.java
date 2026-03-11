package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonService personService;

    @Test
    void findByLogin_whenFound_returnsPerson() {
        Person input = new Person();
        input.setLogin("user");

        Person fromDb = new Person();
        fromDb.setLogin("user");

        when(personRepository.findByLogin("user")).thenReturn(Optional.of(fromDb));

        Person result = personService.findByLogin(input);

        assertSame(fromDb, result);
        verify(personRepository).findByLogin("user");
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void findByLogin_whenNotFound_returnsNull() {
        Person input = new Person();
        input.setLogin("missing");

        when(personRepository.findByLogin("missing")).thenReturn(Optional.empty());

        Person result = personService.findByLogin(input);

        assertNull(result);
        verify(personRepository).findByLogin("missing");
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_encodesPassword_setsUserRole_andSaves() {
        Person person = new Person();
        person.setLogin("newUser");
        person.setPassword("plain");

        when(passwordEncoder.encode("plain")).thenReturn("encoded");

        personService.register(person);

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).save(captor.capture());
        verifyNoMoreInteractions(personRepository);
        verify(passwordEncoder).encode("plain");
        verifyNoMoreInteractions(passwordEncoder);

        Person saved = captor.getValue();
        assertSame(person, saved);
        assertEquals("encoded", saved.getPassword());
        assertEquals("ROLE_USER", saved.getRole());
    }

    @Test
    void getAllPerson_returnsAllPersons() {
        List<Person> persons = List.of(new Person(), new Person());
        when(personRepository.findAll()).thenReturn(persons);

        List<Person> result = personService.getAllPerson();

        assertSame(persons, result);
        verify(personRepository).findAll();
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getPersonId_whenFound_returnsPerson() {
        Person person = new Person();
        when(personRepository.findById(5)).thenReturn(Optional.of(person));

        Person result = personService.getPersonId(5);

        assertSame(person, result);
        verify(personRepository).findById(5);
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void getPersonId_whenNotFound_returnsNull() {
        when(personRepository.findById(5)).thenReturn(Optional.empty());

        Person result = personService.getPersonId(5);

        assertNull(result);
        verify(personRepository).findById(5);
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updatePerson_setsIdAndSaves() {
        Person person = new Person();

        personService.updatePerson(9, person);

        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        verify(personRepository).save(captor.capture());
        verifyNoMoreInteractions(personRepository);
        verifyNoInteractions(passwordEncoder);

        Person saved = captor.getValue();
        assertSame(person, saved);
        assertEquals(9, saved.getId());
    }
}
