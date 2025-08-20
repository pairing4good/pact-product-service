package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product electronicsProduct;
    private Product booksProduct;
    private Product clothingProduct;

    @BeforeEach
    void setUp() {
        // Create test products in different categories
        electronicsProduct = new Product();
        electronicsProduct.setName("Gaming Laptop");
        electronicsProduct.setDescription("High-performance gaming laptop");
        electronicsProduct.setPrice(new BigDecimal("1299.99"));
        electronicsProduct.setStockQuantity(10);
        electronicsProduct.setCategory("Electronics");
        electronicsProduct.setSku("ELEC-LAPTOP-001");
        electronicsProduct.setImageUrl("http://example.com/laptop.jpg");

        booksProduct = new Product();
        booksProduct.setName("Spring Boot Guide");
        booksProduct.setDescription("Complete guide to Spring Boot");
        booksProduct.setPrice(new BigDecimal("39.99"));
        booksProduct.setStockQuantity(50);
        booksProduct.setCategory("Books");
        booksProduct.setSku("BOOK-SPRING-001");
        booksProduct.setImageUrl("http://example.com/book.jpg");

        clothingProduct = new Product();
        clothingProduct.setName("Cotton T-Shirt");
        clothingProduct.setDescription("Premium cotton t-shirt");
        clothingProduct.setPrice(new BigDecimal("19.99"));
        clothingProduct.setStockQuantity(100);
        clothingProduct.setCategory("Clothing");
        clothingProduct.setSku("CLOTH-TSHIRT-001");
        clothingProduct.setImageUrl("http://example.com/tshirt.jpg");

        // Persist test data
        entityManager.persistAndFlush(electronicsProduct);
        entityManager.persistAndFlush(booksProduct);
        entityManager.persistAndFlush(clothingProduct);
    }

    @Test
    void findByCategory_ShouldReturnProductsInCategory() {
        // When
        List<Product> electronicsProducts = productRepository.findByCategory("Electronics");
        List<Product> booksProducts = productRepository.findByCategory("Books");
        List<Product> clothingProducts = productRepository.findByCategory("Clothing");

        // Then
        assertThat(electronicsProducts).hasSize(1);
        assertThat(electronicsProducts.get(0).getName()).isEqualTo("Gaming Laptop");
        assertThat(electronicsProducts.get(0).getCategory()).isEqualTo("Electronics");

        assertThat(booksProducts).hasSize(1);
        assertThat(booksProducts.get(0).getName()).isEqualTo("Spring Boot Guide");
        assertThat(booksProducts.get(0).getCategory()).isEqualTo("Books");

        assertThat(clothingProducts).hasSize(1);
        assertThat(clothingProducts.get(0).getName()).isEqualTo("Cotton T-Shirt");
        assertThat(clothingProducts.get(0).getCategory()).isEqualTo("Clothing");
    }

    @Test
    void findByCategory_WithNonExistentCategory_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByCategory("NonExistentCategory");

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void findByCategory_WithNullCategory_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByCategory(null);

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingProducts() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("gaming");

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Gaming Laptop");
    }

    @Test
    void findByNameContainingIgnoreCase_WithDifferentCase_ShouldReturnMatchingProducts() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("SPRING");

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Spring Boot Guide");
    }

    @Test
    void findByNameContainingIgnoreCase_WithPartialMatch_ShouldReturnMatchingProducts() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("boot");

        // Then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Spring Boot Guide");
    }

    @Test
    void findByNameContainingIgnoreCase_WithNoMatch_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("nonexistent");

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_WithNullName_ShouldReturnEmptyList() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase(null);

        // Then
        assertThat(products).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_WithEmptyString_ShouldReturnAllProducts() {
        // When
        List<Product> products = productRepository.findByNameContainingIgnoreCase("");

        // Then
        assertThat(products).hasSize(3);
    }

    @Test
    void save_ShouldPersistProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("29.99"));
        newProduct.setStockQuantity(25);
        newProduct.setCategory("New Category");
        newProduct.setSku("NEW-PRODUCT-001");

        // When
        Product savedProduct = productRepository.save(newProduct);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("New Product");

        // Verify it's persisted
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("New Product");
    }

    @Test
    void findById_WithExistingId_ShouldReturnProduct() {
        // When
        Optional<Product> foundProduct = productRepository.findById(electronicsProduct.getId());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Gaming Laptop");
        assertThat(foundProduct.get().getCategory()).isEqualTo("Electronics");
    }

    @Test
    void findById_WithNonExistentId_ShouldReturnEmpty() {
        // When
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Then
        assertThat(foundProduct).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllProducts() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products).hasSize(3);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("Gaming Laptop", "Spring Boot Guide", "Cotton T-Shirt");
    }

    @Test
    void deleteById_ShouldRemoveProduct() {
        // Given
        Long productId = electronicsProduct.getId();

        // When
        productRepository.deleteById(productId);

        // Then
        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertThat(deletedProduct).isEmpty();

        // Verify other products still exist
        List<Product> remainingProducts = productRepository.findAll();
        assertThat(remainingProducts).hasSize(2);
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // When
        long count = productRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        // When
        boolean exists = productRepository.existsById(electronicsProduct.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistentId_ShouldReturnFalse() {
        // When
        boolean exists = productRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}