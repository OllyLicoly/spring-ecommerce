package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getAllOrders_returnsAllOrders() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertSame(orders, result);
        verify(orderRepository).findAll();
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderById_whenFound_returnsOrder() {
        Order order = new Order();
        when(orderRepository.findById(10)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(10);

        assertSame(order, result);
        verify(orderRepository).findById(10);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void getOrderById_whenNotFound_throwsEntityNotFound() {
        when(orderRepository.findById(10)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.getOrderById(10)
        );

        assertTrue(ex.getMessage().contains("Order not found: 10"));
        verify(orderRepository).findById(10);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void updateOrder_whenExists_setsIdAndSaves() {
        Order order = new Order();
        when(orderRepository.existsById(5)).thenReturn(true);

        orderService.updateOrder(5, order);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).existsById(5);
        verify(orderRepository).save(captor.capture());
        verifyNoMoreInteractions(orderRepository);

        Order saved = captor.getValue();
        assertSame(order, saved);
        assertEquals(5, saved.getId());
    }

    @Test
    void updateOrder_whenNotExists_throwsEntityNotFound_andDoesNotSave() {
        Order order = new Order();
        when(orderRepository.existsById(5)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.updateOrder(5, order)
        );

        assertTrue(ex.getMessage().contains("Order not found: 5"));
        verify(orderRepository).existsById(5);
        verify(orderRepository, never()).save(any());
        verifyNoMoreInteractions(orderRepository);
    }
}

