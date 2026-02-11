package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.PersonRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Получаем пользоателя из таблицы по логину с формы аутентификации
        Optional<Person> person = personRepository.findByLogin(username);
        //Если пользователь не был найден, выбрасывается исключение, что пользователь не найден. Данное исключение будет поймано Spring Security, и сообщение будет выведено на страницу
        if(person.isEmpty()){
            throw new UsernameNotFoundException("пользователь не найден");
        }
        //Если пользователь найден, то создаем новый объект PersonDetails
        return new PersonDetails(person.get());
    }
}
