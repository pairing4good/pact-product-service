package com.ecommerce.productservice.config;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only load data if the database is empty
        if (productRepository.count() == 0) {
            loadSeedData();
        }
    }
    
    private void loadSeedData() {
        System.out.println("🌱 Loading Product Service seed data...");
        
        // Create comprehensive product catalog with consistent IDs
        Product[] products = {
            // Electronics
            createProduct(1L, "Gaming Laptop Pro", "High-performance gaming laptop with RTX 4080", 
                         new BigDecimal("1299.99"), 15, "Electronics", "TECH-LAPTOP-001"),
            createProduct(2L, "Wireless Gaming Mouse", "Ergonomic wireless gaming mouse with RGB", 
                         new BigDecimal("49.99"), 50, "Electronics", "TECH-MOUSE-001"),
            createProduct(3L, "Mechanical Keyboard", "RGB mechanical keyboard with Cherry MX switches", 
                         new BigDecimal("129.99"), 30, "Electronics", "TECH-KEYBOARD-001"),
            createProduct(4L, "4K Webcam", "Ultra HD webcam for streaming and video calls", 
                         new BigDecimal("89.99"), 25, "Electronics", "TECH-WEBCAM-001"),
            createProduct(5L, "Wireless Headphones", "Noise-cancelling bluetooth headphones", 
                         new BigDecimal("199.99"), 40, "Electronics", "TECH-HEADPHONES-001"),
            
            // Books
            createProduct(6L, "Microservices Architecture", "Complete guide to microservices design patterns", 
                         new BigDecimal("39.99"), 100, "Books", "BOOK-TECH-001"),
            createProduct(7L, "Spring Boot in Action", "Comprehensive Spring Boot development guide", 
                         new BigDecimal("44.99"), 75, "Books", "BOOK-TECH-002"),
            createProduct(8L, "Clean Code", "A handbook of agile software craftsmanship", 
                         new BigDecimal("34.99"), 80, "Books", "BOOK-TECH-003"),
            
            // Home & Garden
            createProduct(9L, "Smart Coffee Maker", "WiFi-enabled programmable coffee maker", 
                         new BigDecimal("149.99"), 20, "Home & Garden", "HOME-COFFEE-001"),
            createProduct(10L, "LED Desk Lamp", "Adjustable LED desk lamp with USB charging", 
                          new BigDecimal("29.99"), 60, "Home & Garden", "HOME-LAMP-001"),
            
            // Sports & Outdoors
            createProduct(11L, "Yoga Mat Premium", "Eco-friendly premium yoga mat with carry strap", 
                          new BigDecimal("24.99"), 45, "Sports & Outdoors", "SPORT-YOGA-001"),
            createProduct(12L, "Water Bottle Insulated", "Stainless steel insulated water bottle 32oz", 
                          new BigDecimal("19.99"), 70, "Sports & Outdoors", "SPORT-BOTTLE-001"),
            
            // Clothing
            createProduct(13L, "Cotton T-Shirt", "Premium cotton crew neck t-shirt", 
                          new BigDecimal("14.99"), 100, "Clothing", "CLOTH-TSHIRT-001"),
            createProduct(14L, "Denim Jeans", "Classic fit denim jeans", 
                          new BigDecimal("59.99"), 35, "Clothing", "CLOTH-JEANS-001"),
            createProduct(15L, "Hoodie Pullover", "Comfortable cotton blend hoodie", 
                          new BigDecimal("39.99"), 55, "Clothing", "CLOTH-HOODIE-001")
        };
        
        for (Product product : products) {
            productRepository.save(product);
        }
        
        System.out.println("✅ Created " + products.length + " products across 5 categories");
        System.out.println("📦 Categories: Electronics, Books, Home & Garden, Sports & Outdoors, Clothing");
    }
    
    private Product createProduct(Long id, String name, String description, BigDecimal price, 
                                 Integer stockQuantity, String category, String sku) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        product.setCategory(category);
        product.setSku(sku);
        return product;
    }
}