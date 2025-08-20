package com.ecommerce.productservice.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {

    private Validator validator;
    private Product product;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("29.99"));
        product.setStockQuantity(10);
        product.setCategory("Electronics");
        product.setSku("TEST-001");
        product.setImageUrl("http://example.com/image.jpg");
    }

    @Test
    void validProduct_ShouldPassValidation() {
        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productWithNullName_ShouldFailValidation() {
        // Given
        product.setName(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }

    @Test
    void productWithEmptyName_ShouldFailValidation() {
        // Given
        product.setName("");

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }

    @Test
    void productWithBlankName_ShouldFailValidation() {
        // Given
        product.setName("   ");

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be blank");
    }

    @Test
    void productWithNullPrice_ShouldFailValidation() {
        // Given
        product.setPrice(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be null");
    }

    @Test
    void productWithZeroPrice_ShouldFailValidation() {
        // Given
        product.setPrice(BigDecimal.ZERO);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must be greater than 0.0");
    }

    @Test
    void productWithNegativePrice_ShouldFailValidation() {
        // Given
        product.setPrice(new BigDecimal("-10.00"));

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must be greater than 0.0");
    }

    @Test
    void productWithNullStockQuantity_ShouldFailValidation() {
        // Given
        product.setStockQuantity(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).contains("must not be null");
    }

    @Test
    void productWithNullDescription_ShouldPassValidation() {
        // Given
        product.setDescription(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productWithNullCategory_ShouldPassValidation() {
        // Given
        product.setCategory(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productWithNullImageUrl_ShouldPassValidation() {
        // Given
        product.setImageUrl(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productWithNullSku_ShouldPassValidation() {
        // Given
        product.setSku(null);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyProduct() {
        // When
        Product emptyProduct = new Product();

        // Then
        assertThat(emptyProduct.getId()).isNull();
        assertThat(emptyProduct.getName()).isNull();
        assertThat(emptyProduct.getDescription()).isNull();
        assertThat(emptyProduct.getPrice()).isNull();
        assertThat(emptyProduct.getStockQuantity()).isNull();
        assertThat(emptyProduct.getCategory()).isNull();
        assertThat(emptyProduct.getImageUrl()).isNull();
        assertThat(emptyProduct.getSku()).isNull();
    }

    @Test
    void parameterizedConstructor_ShouldSetFields() {
        // When
        Product newProduct = new Product("Test Name", "Test Desc", new BigDecimal("15.99"), 25);

        // Then
        assertThat(newProduct.getName()).isEqualTo("Test Name");
        assertThat(newProduct.getDescription()).isEqualTo("Test Desc");
        assertThat(newProduct.getPrice()).isEqualTo(new BigDecimal("15.99"));
        assertThat(newProduct.getStockQuantity()).isEqualTo(25);
        assertThat(newProduct.getId()).isNull();
        assertThat(newProduct.getCategory()).isNull();
        assertThat(newProduct.getImageUrl()).isNull();
        assertThat(newProduct.getSku()).isNull();
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        Product testProduct = new Product();
        Long id = 1L;
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("49.99");
        Integer stockQuantity = 30;
        String category = "Test Category";
        String imageUrl = "http://test.com/image.jpg";
        String sku = "TEST-SKU-001";

        // When
        testProduct.setId(id);
        testProduct.setName(name);
        testProduct.setDescription(description);
        testProduct.setPrice(price);
        testProduct.setStockQuantity(stockQuantity);
        testProduct.setCategory(category);
        testProduct.setImageUrl(imageUrl);
        testProduct.setSku(sku);

        // Then
        assertEquals(id, testProduct.getId());
        assertEquals(name, testProduct.getName());
        assertEquals(description, testProduct.getDescription());
        assertEquals(price, testProduct.getPrice());
        assertEquals(stockQuantity, testProduct.getStockQuantity());
        assertEquals(category, testProduct.getCategory());
        assertEquals(imageUrl, testProduct.getImageUrl());
        assertEquals(sku, testProduct.getSku());
    }

    @Test
    void productWithMinimumValidFields_ShouldPassValidation() {
        // Given
        Product minimalProduct = new Product();
        minimalProduct.setName("Valid Name");
        minimalProduct.setPrice(new BigDecimal("0.01"));
        minimalProduct.setStockQuantity(0);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(minimalProduct);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productWithLargePrice_ShouldPassValidation() {
        // Given
        product.setPrice(new BigDecimal("999999.99"));

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void productWithNegativeStockQuantity_ShouldPassValidation() {
        // Given - Note: there's no @Min validation on stockQuantity in the entity
        product.setStockQuantity(-5);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty(); // No validation constraint on negative stock
    }

    @Test
    void productWithZeroStockQuantity_ShouldPassValidation() {
        // Given
        product.setStockQuantity(0);

        // When
        Set<ConstraintViolation<Product>> violations = validator.validate(product);

        // Then
        assertThat(violations).isEmpty();
    }
}