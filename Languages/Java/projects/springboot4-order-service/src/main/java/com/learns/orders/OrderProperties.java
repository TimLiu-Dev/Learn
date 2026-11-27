package com.learns.orders;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.orders")
public record OrderProperties(
        @NotBlank String defaultCurrency,
        @Positive long maxOrderCents
) {}
