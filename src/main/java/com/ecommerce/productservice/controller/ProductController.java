package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.model.Product;
import com.ecommerce.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product Management", description = "API for managing products including creation, retrieval, updates, and inventory management")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product in the catalog with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product data provided"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
    }
    
    @GetMapping
    @Operation(summary = "Retrieve all products", description = "Returns a list of all products in the catalog")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product using its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found and returned successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found with the provided ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Product> getProductById(
        @Parameter(description = "Unique identifier of the product", required = true, example = "1")
        @PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Retrieves all products that belong to a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully for the category"),
        @ApiResponse(responseCode = "404", description = "No products found for the specified category"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Product>> getProductsByCategory(
        @Parameter(description = "Product category to filter by", required = true, example = "Electronics")
        @PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates an existing product with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid product data provided"),
        @ApiResponse(responseCode = "404", description = "Product not found with the provided ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Product> updateProduct(
        @Parameter(description = "Unique identifier of the product to update", required = true, example = "1")
        @PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @PutMapping("/{id}/stock")
    @Operation(summary = "Update product stock", description = "Updates the inventory quantity for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product stock updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid stock quantity provided"),
        @ApiResponse(responseCode = "404", description = "Product not found with the provided ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Product> updateStock(
        @Parameter(description = "Unique identifier of the product to update stock for", required = true, example = "1")
        @PathVariable Long id, @RequestBody StockUpdateRequest request) {
        Product product = productService.updateStock(id, request.getQuantity());
        return ResponseEntity.ok(product);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Removes a product from the catalog permanently")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found with the provided ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteProduct(
        @Parameter(description = "Unique identifier of the product to delete", required = true, example = "1")
        @PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    public static class StockUpdateRequest {
        private Integer quantity;
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}