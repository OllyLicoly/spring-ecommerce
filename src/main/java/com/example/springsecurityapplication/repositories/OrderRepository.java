package com.example.springsecurityapplication.repositories;

import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    //Получить список заказов конкретного пользователя
    List<Order> findByPerson(Person person);

    //Поиск заказа по последним символам в номере
    @Query(value = "select * from orders where (number LIKE %?1)", nativeQuery = true)
    List<Order> findByNumber (String number);

}

