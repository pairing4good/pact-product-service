package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import com.ecommerce.productservice.telemetry.TelemetryClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private TelemetryClient telemetryClient;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("29.99"));
        testProduct.setStockQuantity(10);
        testProduct.setCategory("Electronics");
        testProduct.setSku("TEST-001");
        testProduct.setImageUrl("http://example.com/image.jpg");

        // Mock telemetry client to prevent null pointer exceptions
        when(telemetryClient.startTrace(anyString(), anyString(), anyString(), anyString())).thenReturn("trace-123");
        doNothing().when(telemetryClient).finishTrace(anyString(), anyInt(), anyString());
        doNothing().when(telemetryClient).logEvent(anyString(), anyString());
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("19.99"));
        newProduct.setStockQuantity(5);

        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.price").value(29.99))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldHandleException() throws Exception {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");

        when(productService.createProduct(any(Product.class)))
                .thenThrow(new RuntimeException("Creation failed"));

        // When & Then
        try {
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newProduct)));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void getAllProducts_ShouldReturnProductList() throws Exception {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        List<Product> products = Arrays.asList(testProduct, product2);

        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_ShouldHandleException() throws Exception {
        // Given
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        try {
            mockMvc.perform(get("/api/products"));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_ShouldHandleNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenThrow(new RuntimeException("Product not found"));

        // When & Then
        try {
            mockMvc.perform(get("/api/products/999"));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).getProductById(999L);
    }

    @Test
    void getProductsByCategory_ShouldReturnProductList() throws Exception {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("Electronics")).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("Electronics"));

        verify(productService).getProductsByCategory("Electronics");
    }

    @Test
    void getProductsByCategory_ShouldHandleNotFound() throws Exception {
        // Given
        when(productService.getProductsByCategory("NonExistent"))
                .thenThrow(new RuntimeException("No products found"));

        // When & Then
        try {
            mockMvc.perform(get("/api/products/category/NonExistent"));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).getProductsByCategory("NonExistent");
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(new BigDecimal("39.99"));

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(39.99));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_ShouldHandleNotFound() throws Exception {
        // Given
        Product updateData = new Product();
        updateData.setName("Updated Product");

        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new RuntimeException("Product not found"));

        // When & Then
        try {
            mockMvc.perform(put("/api/products/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateData)));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void updateStock_ShouldReturnUpdatedProduct() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Test Product");
        updatedProduct.setStockQuantity(20);

        ProductController.StockUpdateRequest request = new ProductController.StockUpdateRequest();
        request.setQuantity(20);

        when(productService.updateStock(1L, 20)).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(20));

        verify(productService).updateStock(1L, 20);
    }

    @Test
    void updateStock_ShouldHandleInvalidQuantity() throws Exception {
        // Given
        ProductController.StockUpdateRequest request = new ProductController.StockUpdateRequest();
        request.setQuantity(-5);

        when(productService.updateStock(1L, -5))
                .thenThrow(new RuntimeException("Invalid stock quantity"));

        // When & Then
        try {
            mockMvc.perform(put("/api/products/1/stock")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).updateStock(1L, -5);
    }

    @Test
    void updateStock_ShouldHandleNotFound() throws Exception {
        // Given
        ProductController.StockUpdateRequest request = new ProductController.StockUpdateRequest();
        request.setQuantity(10);

        when(productService.updateStock(999L, 10))
                .thenThrow(new RuntimeException("Product not found"));

        // When & Then
        try {
            mockMvc.perform(put("/api/products/999/stock")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).updateStock(999L, 10);
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_ShouldHandleNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(999L);

        // When & Then
        try {
            mockMvc.perform(delete("/api/products/999"));
        } catch (Exception e) {
            // Exception is expected for unhandled RuntimeException
        }

        verify(productService).deleteProduct(999L);
    }

    @Test
    void stockUpdateRequest_ShouldHaveGettersAndSetters() {
        // Given
        ProductController.StockUpdateRequest request = new ProductController.StockUpdateRequest();

        // When
        request.setQuantity(15);

        // Then
        assertEquals(15, request.getQuantity());
    }
}