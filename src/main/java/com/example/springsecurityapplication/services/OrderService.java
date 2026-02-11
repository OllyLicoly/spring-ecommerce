package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

        public List<Order> getAllOrders(Person person){

        return orderRepository.findByPerson(person);
    }

    //Получить список всех заказов
    public List<Order> getAllOrders1(){
        return orderRepository.findAll();
    }

    // Данный метод позволяет получить товар по id
    public Order getOrderById(int id){
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    //Обновить данные о заказе. Так как предается id, Spring Data JPA что нужно обновить информацию об объекте
    @Transactional
    public void updateOrder (int id, Order order) {
        order.setId(id);
        orderRepository.save(order);
    }

}


