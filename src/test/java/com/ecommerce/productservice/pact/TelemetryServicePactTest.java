package com.ecommerce.productservice.pact;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.LambdaDsl;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
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
 * NOTE: This test is currently experiencing Pact 4.6.4 API compatibility issues.
 * The contract definition and test structure are correct, but the method signatures
 * need to be updated for the newer Pact version.
 * 
 * Expected behavior:
 * - TelemetryClient sends POST requests to /api/telemetry/events
 * - Request body contains only fields actually sent by the client
 * - Mock server validates request structure matches contract
 * - Contract file generated in build/pacts/ directory
 * 
 * TODO: Update method signatures to work with Pact 4.6.4 V4Pact API
 * TODO: Verify contract file generation and content
 */
@ExtendWith(PactConsumerTestExt.class)
class TelemetryServicePactTest {

    @Pact(consumer = "product-service", provider = "telemetry-service")
    public RequestResponsePact startTracePact(PactDslWithProvider builder) {
        return builder
            .given("telemetry service is available")
            .uponReceiving("a telemetry event for starting a trace")
            .path("/api/telemetry/events")
            .method("POST")
            .headers(Map.of(
                "Content-Type", "application/json"
            ))
            .body(LambdaDsl.newJsonBody((body) -> body
                // Following "conservative in what you send" principle
                // Only include fields actually sent by TelemetryClient
                .stringType("traceId", "trace_12345")
                .stringType("spanId", "span_67890")
                .stringType("serviceName", "product-service")
                .stringType("operation", "createProduct")
                .stringType("eventType", "SPAN")
                .stringType("timestamp", "2023-01-01T00:00:00")
                .stringType("status", "SUCCESS")
                .stringType("httpMethod", "POST")
                .stringType("httpUrl", "/api/products")
                .stringType("userId", "")
            ).build())
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "startTracePact")
    void testStartTrace(MockServer mockServer) {
        // Arrange: Create TelemetryClient with mock server URL
        TelemetryClient telemetryClient = createTelemetryClientWithMockUrl(mockServer.getUrl());
        
        // Act: Start a trace - this should send telemetry data
        assertDoesNotThrow(() -> {
            telemetryClient.startTrace("createProduct", "POST", "/api/products", "");
        });
        
        // Assert: The contract verifies the HTTP interaction occurred as expected
        // No additional assertions needed as Pact verifies the HTTP call matches the contract
    }

    /**
     * Helper method to create a TelemetryClient that uses the mock server URL
     */
    private TelemetryClient createTelemetryClientWithMockUrl(String mockServerUrl) {
        TelemetryClient client = new TelemetryClient();
        try {
            java.lang.reflect.Field urlField = TelemetryClient.class.getDeclaredField("telemetryServiceUrl");
            urlField.setAccessible(true);
            urlField.set(client, mockServerUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set mock server URL", e);
        }
        return client;
    }
}