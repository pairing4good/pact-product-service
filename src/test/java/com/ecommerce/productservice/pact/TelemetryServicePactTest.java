package com.ecommerce.productservice.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.ecommerce.productservice.telemetry.TelemetryClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Consumer contract test for TelemetryService.
 * 
 * This test verifies that the product-service correctly sends telemetry data 
 * to the telemetry-service endpoint according to the consumer contract.
 * 
 * CONTRACT GENERATION: This test generates contracts in build/pacts/ directory
 * CONTRACT PUBLISHING: Use './gradlew pactPublish' to publish to Pactflow broker
 * 
 * Expected behavior:
 * - TelemetryClient sends POST requests to /api/telemetry/events
 * - Request body contains fields actually sent by the client (conservative approach)
 * - Mock server validates request structure matches contract exactly
 * - Contract file generated as product-service-telemetry-service.json
 * 
 * RESOLVED: Updated to use Pact 4.6.4 V4 API with proper data type matching
 * - V4Pact return type instead of RequestResponsePact  
 * - PactBuilder parameter instead of PactDslWithProvider
 * - Array format for LocalDateTime timestamp serialization
 * - Reflection-based mock server URL injection for testing
 */
@ExtendWith(PactConsumerTestExt.class)
class TelemetryServicePactTest {

    @Pact(consumer = "product-service", provider = "telemetry-service")
    public V4Pact startTracePact(PactBuilder builder) {
        return builder
            .expectsToReceiveHttpInteraction("a telemetry event for starting a trace", interaction -> interaction
                .withRequest(request -> request
                    .path("/api/telemetry/events")
                    .method("POST")
                    .header("Content-Type", "application/json")
                    .body(LambdaDsl.newJsonBody((body) -> body
                        // Following "conservative in what you send" principle
                        // Only include fields actually sent by TelemetryClient
                        .stringType("traceId")
                        .stringType("spanId")
                        .stringType("serviceName", "product-service")
                        .stringType("operation", "createProduct")
                        .stringType("eventType", "SPAN")
                        .array("timestamp", arr -> {
                            // LocalDateTime is serialized as [year, month, day, hour, minute, second, nanosecond]
                            arr.numberType(2025) // year
                               .numberType(9)    // month  
                               .numberType(11)   // day
                               .numberType(13)   // hour
                               .numberType(26)   // minute
                               .numberType(6)    // second
                               .numberType(574625898); // nanosecond
                        })
                        .stringType("status", "SUCCESS")
                        .stringType("httpMethod", "POST")
                        .stringType("httpUrl", "/api/products")
                        .stringType("userId", "")
                    ).build())
                )
                .willRespondWith(response -> response
                    .status(200)
                    .header("Content-Type", "application/json")
                )
            )
            .given("telemetry service is available")
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startTracePact")
    void testStartTrace(MockServer mockServer) throws InterruptedException {
        // Arrange: Create TelemetryClient with mock server URL
        TelemetryClient telemetryClient = createTelemetryClientWithMockUrl(mockServer.getUrl());
        
        // Act: Start a trace - this should send telemetry data
        assertDoesNotThrow(() -> {
            telemetryClient.startTrace("createProduct", "POST", "/api/products", "");
        });
        
        // Wait for asynchronous WebClient call to complete
        Thread.sleep(1000);
        
        // Assert: The contract verifies the HTTP interaction occurred as expected
        // No additional assertions needed as Pact verifies the HTTP call matches the contract
    }

    /**
     * Helper method to create a TelemetryClient that uses the mock server URL
     */
    private TelemetryClient createTelemetryClientWithMockUrl(String mockServerUrl) {
        TelemetryClient client = new TelemetryClient();
        try {
            // Set the mock server URL via reflection
            java.lang.reflect.Field urlField = TelemetryClient.class.getDeclaredField("telemetryServiceUrl");
            urlField.setAccessible(true);
            urlField.set(client, mockServerUrl);
            
            // Set a service name for testing (since @Value annotation won't work in tests)
            java.lang.reflect.Field serviceNameField = TelemetryClient.class.getDeclaredField("serviceName");
            serviceNameField.setAccessible(true);
            serviceNameField.set(client, "product-service");
            
            // Verify the URL was set correctly for debugging
            System.out.println("Mock server URL set to: " + urlField.get(client));
            System.out.println("Service name set to: " + serviceNameField.get(client));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock server URL", e);
        }
        return client;
    }
}