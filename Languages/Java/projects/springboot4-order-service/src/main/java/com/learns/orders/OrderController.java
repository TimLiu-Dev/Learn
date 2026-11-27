package com.learns.orders;

import com.learns.orders.OrderDtos.CreateOrderRequest;
import com.learns.orders.OrderDtos.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    public static class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(long id) {
            super("Order " + id + " was not found");
        }
    }

    private final OrderRepository repository;
    private final OrderProperties properties;

    public OrderController(OrderRepository repository, OrderProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    @PostMapping(version = "1.0")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        if (req.amountCents() > properties.maxOrderCents()) {
            throw new IllegalArgumentException("Order exceeds configured maxOrderCents=" + properties.maxOrderCents());
        }
        String currency = (req.currency() == null || req.currency().isBlank())
                ? properties.defaultCurrency()
                : req.currency().trim().toUpperCase();

        OrderResponse saved = repository.insert(req.customerId(), req.sku(), req.amountCents(), currency);
        URI location = URI.create("/api/orders/" + saved.id());
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping(path = "/{id}", version = "1.0")
    public OrderResponse getById(@PathVariable long id) {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
