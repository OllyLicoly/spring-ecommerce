package com.example.springsecurityapplication.it;

import com.example.springsecurityapplication.models.Category;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.ProductRepository;
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
class ProductRepositoryIT {

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

    @Autowired ProductRepository productRepository;
    @Autowired TestEntityManager em;

    private Category category;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        Category c = new Category();
        c.setName("Test category");
        category = em.persistAndFlush(c);
    }

    private Product newProduct(String title, float price) {
        Product p = new Product();
        p.setTitle(title);
        p.setPrice(price);

        // обязательные поля по валидации
        p.setDescription("test description");
        p.setWarehouse("test warehouse");
        p.setSeller("test seller");

        p.setCategory(category);
        return p;
    }

    @Test
    void findByTitleContainingIgnoreCase_findsRegardlessOfCase() {
        productRepository.saveAll(List.of(
                newProduct("Apple iPhone", 1000f),
                newProduct("apple pie", 10f),
                newProduct("Banana", 5f)
        ));

        List<Product> found = productRepository.findByTitleContainingIgnoreCase("APPLE");

        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(p -> p.getTitle().toLowerCase().contains("apple")));
    }

    @Test
    void findByTitleOrderByPriceAsc_filtersByPriceAndSorts() {
        productRepository.saveAll(List.of(
                newProduct("Milk chocolate", 5f),
                newProduct("Dark Chocolate", 20f),
                newProduct("Chocolate deluxe", 100f)
        ));

        List<Product> found = productRepository.findByTitleOrderByPriceAsc("choco", 1f, 50f);

        assertEquals(2, found.size());
        assertEquals(5f, found.get(0).getPrice());
        assertEquals(20f, found.get(1).getPrice());
    }
}