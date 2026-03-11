package com.example.springsecurityapplication.services;

import com.example.springsecurityapplication.models.Category;
import com.example.springsecurityapplication.models.Product;
import com.example.springsecurityapplication.repositories.ProductRepository;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllProduct_returnsAllProducts() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProduct();

        assertSame(products, result);
        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getProductId_whenFound_returnsProduct() {
        Product product = new Product();
        when(productRepository.findById(7)).thenReturn(Optional.of(product));

        Product result = productService.getProductId(7);

        assertSame(product, result);
        verify(productRepository).findById(7);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getProductId_whenNotFound_returnsNull() {
        when(productRepository.findById(7)).thenReturn(Optional.empty());

        Product result = productService.getProductId(7);

        assertNull(result);
        verify(productRepository).findById(7);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void saveProduct_setsCategoryAndSaves() {
        Product product = new Product();
        Category category = new Category();

        productService.saveProduct(product, category);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        verifyNoMoreInteractions(productRepository);

        Product saved = captor.getValue();
        assertSame(product, saved);
        assertSame(category, saved.getCategory());
    }

    @Test
    void updateProduct_setsIdAndSaves() {
        Product product = new Product();

        productService.updateProduct(12, product);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        verifyNoMoreInteractions(productRepository);

        Product saved = captor.getValue();
        assertSame(product, saved);
        assertEquals(12, saved.getId());
    }

    @Test
    void deleteProduct_deletesById() {
        productService.deleteProduct(3);

        verify(productRepository).deleteById(3);
        verifyNoMoreInteractions(productRepository);
    }
}
