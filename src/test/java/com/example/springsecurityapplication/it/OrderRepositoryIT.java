package com.example.springsecurityapplication.it;

import com.example.springsecurityapplication.models.Category;
import com.example.springsecurityapplication.models.Order;
import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
        r.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired OrderRepository orderRepository;
    @Autowired TestEntityManager em;

    private Person alice;
    private Person felix;
    private Product product;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        alice = new Person();
        alice.setLogin("alice");
        alice.setPassword("xxxxx");
        alice.setRole("ROLE_USER");
        alice = em.persistAndFlush(alice);

        felix = new Person();
        felix.setLogin("felix");
        felix.setPassword("yyyyy");
        felix.setRole("ROLE_USER");
        felix = em.persistAndFlush(felix);

        Category cat = new Category();
        cat.setName("Test category");
        cat = em.persistAndFlush(cat);

        Product p = new Product();
        p.setTitle("Test product");
        p.setPrice(10f);
        p.setDescription("test description");
        p.setWarehouse("test warehouse");
        p.setSeller("test seller");
        p.setCategory(cat);
        product = em.persistAndFlush(p);
    }

    private Order newOrder(Person person, String number) {
        Order o = new Order();
        o.setPerson(person);
        o.setNumber(number);
        o.setProduct(product);
        return o;
    }

    @Test
    void findByPerson_returnsOnlyOrdersOfThatPerson() {
        orderRepository.saveAll(List.of(
                newOrder(alice, "ORD-0001"),
                newOrder(alice, "ORD-0002"),
                newOrder(felix, "ORD-0003")
        ));

        List<Order> aliceOrders = orderRepository.findByPerson(alice);

        assertEquals(2, aliceOrders.size());
        assertTrue(aliceOrders.stream().allMatch(o -> o.getPerson().getId() == alice.getId()));
    }

    @Test
    void findByNumber_findsByLastDigits() {
        orderRepository.saveAll(List.of(
                newOrder(alice, "ORDER-12345"),
                newOrder(felix, "ORDER-99999"),
                newOrder(alice, "INV-54321")
        ));

        List<Order> found = orderRepository.findByNumber("345"); // LIKE %345

        assertEquals(1, found.size());
        assertEquals("ORDER-12345", found.get(0).getNumber());
    }
}