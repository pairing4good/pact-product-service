package com.ecommerce.productservice.pact.config;

import com.ecommerce.productservice.telemetry.TelemetryClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class PactTestConfig {
    
    /**
     * Creates a test-specific TelemetryClient that can be configured with mock server URLs
     * This bean will override the main TelemetryClient during Pact testing
     */
    @Bean
    @Primary
    public TelemetryClient mockableTelemetryClient() {
        return new TestableTelemetryClient();
    }
    
    /**
     * Testable version of TelemetryClient that allows URL configuration
     */
    public static class TestableTelemetryClient extends TelemetryClient {
        
        // Override the private method behavior by exposing URL setting capability
        public void configureMockServerUrl(String mockServerUrl) {
            try {
                java.lang.reflect.Field urlField = TelemetryClient.class.getDeclaredField("telemetryServiceUrl");
                urlField.setAccessible(true);
                urlField.set(this, mockServerUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to set mock server URL", e);
            }
        }
    }
}