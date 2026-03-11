package com.example.springsecurityapplication.it;

import com.example.springsecurityapplication.models.*;
import com.example.springsecurityapplication.repositories.CartRepository;
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

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CartRepositoryIT {

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

    @Autowired CartRepository cartRepository;
    @Autowired TestEntityManager em;

    private Person alice;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        cartRepository.deleteAll();

        alice = new Person();
        alice.setLogin("alice");
        alice.setPassword("x");
        alice.setRole("ROLE_USER");
        alice = em.persistAndFlush(alice);

        Category category = new Category();
        category.setName("Test category");
        category = em.persistAndFlush(category);

        Product p1 = new Product();
        p1.setTitle("Product 1");
        p1.setPrice(10f);
        p1.setDescription("d");
        p1.setWarehouse("w");
        p1.setSeller("s");
        p1.setCategory(category);
        product1 = em.persistAndFlush(p1);

        Product p2 = new Product();
        p2.setTitle("Product 2");
        p2.setPrice(20f);
        p2.setDescription("d");
        p2.setWarehouse("w");
        p2.setSeller("s");
        p2.setCategory(category);
        product2 = em.persistAndFlush(p2);
    }

    @Test
    void findByPersonId_returnsCartsOfThatPerson() {
        Cart c1 = new Cart();
        c1.setPersonId(alice.getId());
        c1.setProductId(product1.getId());

        Cart c2 = new Cart();
        c2.setPersonId(alice.getId());
        c2.setProductId(product2.getId());

        cartRepository.saveAll(List.of(c1, c2));
        em.flush();
        em.clear();

        List<Cart> carts = cartRepository.findByPersonId(alice.getId());

        assertEquals(2, carts.size());
        assertTrue(carts.stream().allMatch(c -> c.getPersonId() == alice.getId()));
    }

    @Test
    void deleteCartByProductId_deletesRowsFromJoinTable() {
        Cart c1 = new Cart();
        c1.setPersonId(alice.getId());
        c1.setProductId(product1.getId());

        Cart c2 = new Cart();
        c2.setPersonId(alice.getId());
        c2.setProductId(product2.getId());

        cartRepository.saveAll(List.of(c1, c2));
        em.flush();

        long before = countProductCartRowsByProductId(product1.getId());
        assertTrue(before > 0);

        cartRepository.deleteCartByProductId(product1.getId());
        em.flush();

        long after = countProductCartRowsByProductId(product1.getId());
        assertEquals(0, after);
    }

    // helper для проверки факта удаления из product_cart
    private long countProductCartRowsByProductId(int productId) {
        Object single = em.getEntityManager()
                .createNativeQuery("select count(*) from product_cart where product_id = :pid")
                .setParameter("pid", productId)
                .getSingleResult();

        if (single instanceof BigInteger bi) return bi.longValue();
        if (single instanceof Number n) return n.longValue();
        return Long.parseLong(single.toString());
    }

}