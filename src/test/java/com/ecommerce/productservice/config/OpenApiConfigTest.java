package com.ecommerce.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void productServiceOpenAPI_ShouldReturnConfiguredOpenAPI() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
    }

    @Test
    void productServiceOpenAPI_ShouldHaveCorrectInfo() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();
        Info info = openAPI.getInfo();

        // Then
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Product Service API");
        assertThat(info.getVersion()).isEqualTo("1.0");
        assertThat(info.getDescription()).contains("This API exposes endpoints for product catalog management");
        assertThat(info.getDescription()).contains("e-commerce microservices ecosystem");
        assertThat(info.getDescription()).contains("product CRUD operations");
        assertThat(info.getDescription()).contains("inventory management");
        assertThat(info.getDescription()).contains("search, filtering by category");
        assertThat(info.getDescription()).contains("pricing");
    }

    @Test
    void productServiceOpenAPI_ShouldHaveCorrectContact() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Then
        assertThat(contact).isNotNull();
        assertThat(contact.getEmail()).isEqualTo("support@ecommerce.com");
        assertThat(contact.getName()).isEqualTo("E-Commerce Support");
        assertThat(contact.getUrl()).isEqualTo("https://www.ecommerce.com");
    }

    @Test
    void productServiceOpenAPI_ShouldHaveCorrectLicense() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Then
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://choosealicense.com/licenses/mit/");
    }

    @Test
    void productServiceOpenAPI_ShouldHaveCorrectServers() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();
        List<Server> servers = openAPI.getServers();

        // Then
        assertThat(servers).isNotNull();
        assertThat(servers).hasSize(1);
        
        Server devServer = servers.get(0);
        assertThat(devServer.getUrl()).isEqualTo("http://localhost:8082");
        assertThat(devServer.getDescription()).isEqualTo("Server URL in Development environment");
    }

    @Test
    void productServiceOpenAPI_ShouldBeCompleteConfiguration() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();

        // Then - Verify all main components are present
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getServers()).isNotEmpty();
        
        // Verify the configuration is suitable for Swagger UI
        assertThat(openAPI.getInfo().getTitle()).isNotBlank();
        assertThat(openAPI.getInfo().getVersion()).isNotBlank();
        assertThat(openAPI.getInfo().getDescription()).isNotBlank();
        
        // Verify contact information is complete
        Contact contact = openAPI.getInfo().getContact();
        assertThat(contact.getEmail()).isNotBlank();
        assertThat(contact.getName()).isNotBlank();
        assertThat(contact.getUrl()).isNotBlank();
        
        // Verify license information is complete
        License license = openAPI.getInfo().getLicense();
        assertThat(license.getName()).isNotBlank();
        assertThat(license.getUrl()).isNotBlank();
        
        // Verify server information is complete
        Server server = openAPI.getServers().get(0);
        assertThat(server.getUrl()).isNotBlank();
        assertThat(server.getDescription()).isNotBlank();
    }

    @Test
    void productServiceOpenAPI_ShouldUseCorrectPortForProductService() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();
        Server devServer = openAPI.getServers().get(0);

        // Then
        assertThat(devServer.getUrl()).contains("8082"); // Product service port
        assertThat(devServer.getUrl()).startsWith("http://localhost");
    }

    @Test
    void productServiceOpenAPI_InfoShouldBeProductServiceSpecific() {
        // When
        OpenAPI openAPI = openApiConfig.productServiceOpenAPI();
        Info info = openAPI.getInfo();

        // Then
        assertThat(info.getTitle()).contains("Product Service");
        assertThat(info.getDescription()).contains("product catalog management");
        assertThat(info.getDescription()).contains("product CRUD operations");
        assertThat(info.getDescription()).contains("inventory management");
        
        // Should not contain user-service specific terms
        assertThat(info.getTitle()).doesNotContainIgnoringCase("user");
        assertThat(info.getDescription()).doesNotContainIgnoringCase("user management");
        assertThat(info.getDescription()).doesNotContainIgnoringCase("authentication");
    }

    @Test
    void openApiConfig_ShouldBeInstantiable() {
        // When
        OpenApiConfig config = new OpenApiConfig();

        // Then
        assertThat(config).isNotNull();
        assertThat(config.productServiceOpenAPI()).isNotNull();
    }
}