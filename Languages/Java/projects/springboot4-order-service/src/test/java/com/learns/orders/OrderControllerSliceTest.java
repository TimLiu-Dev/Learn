package com.learns.orders;

import com.learns.orders.OrderDtos.OrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(OrderController.class)
@AutoConfigureRestTestClient
@EnableConfigurationProperties(OrderProperties.class)
@TestPropertySource(properties = {
        "app.orders.default-currency=USD",
        "app.orders.max-order-cents=5000000"
})
class OrderControllerSliceTest {

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private OrderRepository orderRepository;

    @Test
    void createOrderReturns201AndLocationHeader() {
        var stubbed = new OrderResponse(1001L, "CUST-9", "SKU-PRO", 2500L, "USD", Instant.parse("2026-11-24T12:00:00Z"));
        when(orderRepository.insert(eq("CUST-9"), eq("SKU-PRO"), eq(2500L), eq("USD"))).thenReturn(stubbed);

        restTestClient.post()
                .uri("/api/orders")
                .header("X-API-Version", "1.0")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                      {"customer_id":"CUST-9","sku":"SKU-PRO","amount_cents":2500}
                      """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/orders/1001")
                .expectBody()
                .jsonPath("$.id").isEqualTo(1001)
                .jsonPath("$.currency").isEqualTo("USD");
    }

    @Test
    void missingOrderReturnsRfc9457ProblemDetail() {
        when(orderRepository.findById(9999L)).thenReturn(Optional.empty());

        restTestClient.get()
                .uri("/api/orders/9999")
                .header("X-API-Version", "1.0")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Order Not Found")
                .jsonPath("$.errorCode").isEqualTo("ORD_404");
    }
}
