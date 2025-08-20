package com.ecommerce.productservice.config;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataLoaderTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(productRepository);
    }

    @Test
    void run_WhenDatabaseIsEmpty_ShouldLoadSeedData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        verify(productRepository).count();
        
        // Capture all products saved
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        assertThat(savedProducts).hasSize(15);
        
        // Verify specific products from different categories
        assertThat(savedProducts)
            .extracting(Product::getName)
            .contains(
                "Gaming Laptop Pro",
                "Wireless Gaming Mouse", 
                "Microservices Architecture",
                "Smart Coffee Maker",
                "Yoga Mat Premium",
                "Cotton T-Shirt"
            );
            
        // Verify categories are correctly set
        assertThat(savedProducts)
            .extracting(Product::getCategory)
            .contains("Electronics", "Books", "Home & Garden", "Sports & Outdoors", "Clothing");
    }

    @Test
    void run_WhenDatabaseHasData_ShouldNotLoadSeedData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(5L);

        // When
        dataLoader.run();

        // Then
        verify(productRepository).count();
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void loadSeedData_ShouldCreateProductsWithCorrectElectronicsData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Find and verify the Gaming Laptop Pro
        Product gamingLaptop = savedProducts.stream()
            .filter(p -> "Gaming Laptop Pro".equals(p.getName()))
            .findFirst()
            .orElseThrow();
            
        assertThat(gamingLaptop.getDescription()).isEqualTo("High-performance gaming laptop with RTX 4080");
        assertThat(gamingLaptop.getPrice()).isEqualTo(new BigDecimal("1299.99"));
        assertThat(gamingLaptop.getStockQuantity()).isEqualTo(15);
        assertThat(gamingLaptop.getCategory()).isEqualTo("Electronics");
        assertThat(gamingLaptop.getSku()).isEqualTo("TECH-LAPTOP-001");
    }

    @Test
    void loadSeedData_ShouldCreateProductsWithCorrectBooksData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Find and verify a book
        Product microservicesBook = savedProducts.stream()
            .filter(p -> "Microservices Architecture".equals(p.getName()))
            .findFirst()
            .orElseThrow();
            
        assertThat(microservicesBook.getDescription()).isEqualTo("Complete guide to microservices design patterns");
        assertThat(microservicesBook.getPrice()).isEqualTo(new BigDecimal("39.99"));
        assertThat(microservicesBook.getStockQuantity()).isEqualTo(100);
        assertThat(microservicesBook.getCategory()).isEqualTo("Books");
        assertThat(microservicesBook.getSku()).isEqualTo("BOOK-TECH-001");
    }

    @Test
    void loadSeedData_ShouldCreateProductsWithCorrectHomeGardenData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Find and verify a home & garden product
        Product coffeeMaker = savedProducts.stream()
            .filter(p -> "Smart Coffee Maker".equals(p.getName()))
            .findFirst()
            .orElseThrow();
            
        assertThat(coffeeMaker.getDescription()).isEqualTo("WiFi-enabled programmable coffee maker");
        assertThat(coffeeMaker.getPrice()).isEqualTo(new BigDecimal("149.99"));
        assertThat(coffeeMaker.getStockQuantity()).isEqualTo(20);
        assertThat(coffeeMaker.getCategory()).isEqualTo("Home & Garden");
        assertThat(coffeeMaker.getSku()).isEqualTo("HOME-COFFEE-001");
    }

    @Test
    void loadSeedData_ShouldCreateProductsWithCorrectSportsOutdoorsData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Find and verify a sports & outdoors product
        Product yogaMat = savedProducts.stream()
            .filter(p -> "Yoga Mat Premium".equals(p.getName()))
            .findFirst()
            .orElseThrow();
            
        assertThat(yogaMat.getDescription()).isEqualTo("Eco-friendly premium yoga mat with carry strap");
        assertThat(yogaMat.getPrice()).isEqualTo(new BigDecimal("24.99"));
        assertThat(yogaMat.getStockQuantity()).isEqualTo(45);
        assertThat(yogaMat.getCategory()).isEqualTo("Sports & Outdoors");
        assertThat(yogaMat.getSku()).isEqualTo("SPORT-YOGA-001");
    }

    @Test
    void loadSeedData_ShouldCreateProductsWithCorrectClothingData() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Find and verify a clothing product
        Product tShirt = savedProducts.stream()
            .filter(p -> "Cotton T-Shirt".equals(p.getName()))
            .findFirst()
            .orElseThrow();
            
        assertThat(tShirt.getDescription()).isEqualTo("Premium cotton crew neck t-shirt");
        assertThat(tShirt.getPrice()).isEqualTo(new BigDecimal("14.99"));
        assertThat(tShirt.getStockQuantity()).isEqualTo(100);
        assertThat(tShirt.getCategory()).isEqualTo("Clothing");
        assertThat(tShirt.getSku()).isEqualTo("CLOTH-TSHIRT-001");
    }

    @Test
    void loadSeedData_ShouldCreateProductsWithUniqueIds() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Verify all products have unique IDs (set in createProduct helper method)
        assertThat(savedProducts)
            .extracting(Product::getId)
            .hasSize(15)
            .doesNotContainNull()
            .doesNotHaveDuplicates();
            
        // Verify specific IDs for known products
        Product gamingLaptop = savedProducts.stream()
            .filter(p -> "Gaming Laptop Pro".equals(p.getName()))
            .findFirst()
            .orElseThrow();
        assertThat(gamingLaptop.getId()).isEqualTo(1L);
        
        Product hoodie = savedProducts.stream()
            .filter(p -> "Hoodie Pullover".equals(p.getName()))
            .findFirst()
            .orElseThrow();
        assertThat(hoodie.getId()).isEqualTo(15L);
    }

    @Test
    void loadSeedData_ShouldCreateCorrectNumberOfProductsPerCategory() throws Exception {
        // Given
        when(productRepository.count()).thenReturn(0L);

        // When
        dataLoader.run();

        // Then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(15)).save(productCaptor.capture());
        
        List<Product> savedProducts = productCaptor.getAllValues();
        
        // Verify product count per category
        long electronicsCount = savedProducts.stream()
            .filter(p -> "Electronics".equals(p.getCategory()))
            .count();
        assertThat(electronicsCount).isEqualTo(5);
        
        long booksCount = savedProducts.stream()
            .filter(p -> "Books".equals(p.getCategory()))
            .count();
        assertThat(booksCount).isEqualTo(3);
        
        long homeGardenCount = savedProducts.stream()
            .filter(p -> "Home & Garden".equals(p.getCategory()))
            .count();
        assertThat(homeGardenCount).isEqualTo(2);
        
        long sportsCount = savedProducts.stream()
            .filter(p -> "Sports & Outdoors".equals(p.getCategory()))
            .count();
        assertThat(sportsCount).isEqualTo(2);
        
        long clothingCount = savedProducts.stream()
            .filter(p -> "Clothing".equals(p.getCategory()))
            .count();
        assertThat(clothingCount).isEqualTo(3);
    }
}