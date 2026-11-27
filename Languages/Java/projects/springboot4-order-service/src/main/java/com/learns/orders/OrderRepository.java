package com.learns.orders;

import com.learns.orders.OrderDtos.OrderResponse;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcClient jdbcClient;

    public OrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Transactional
    public OrderResponse insert(String customerId, String sku, long amountCents, String currency) {
        return jdbcClient.sql("""
                INSERT INTO orders (customer_id, sku, amount_cents, currency)
                VALUES (:customerId, :sku, :amountCents, :currency)
                RETURNING id, customer_id, sku, amount_cents, currency, created_at
                """)
                .param("customerId", customerId)
                .param("sku", sku)
                .param("amountCents", amountCents)
                .param("currency", currency)
                .query(OrderResponse.class)
                .single();
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> findById(long id) {
        return jdbcClient.sql("""
                SELECT id, customer_id, sku, amount_cents, currency, created_at
                FROM orders
                WHERE id = :id
                """)
                .param("id", id)
                .query(OrderResponse.class)
                .optional();
    }
}
