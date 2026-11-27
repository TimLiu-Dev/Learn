package com.learns.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.Nullable;
import java.time.Instant;

public final class OrderDtos {
    private OrderDtos() {}

    public record CreateOrderRequest(
            @JsonProperty("customer_id") @NotBlank String customerId,
            @NotBlank String sku,
            @JsonProperty("amount_cents") @Positive long amountCents,
            @Nullable String currency
    ) {}

    public record OrderResponse(
            long id,
            String customerId,
            String sku,
            long amountCents,
            String currency,
            Instant createdAt
    ) {}
}
