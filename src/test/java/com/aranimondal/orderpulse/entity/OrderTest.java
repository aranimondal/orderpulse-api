package com.aranimondal.orderpulse.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("Builder creates entity with all fields set")
    void builder_createsEntityCorrectly() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 20, 10, 0, 0);
        Order order = Order.builder()
                .orderId("ORD-2001")
                .customerName("Test User")
                .itemName("Test Product")
                .quantity(3)
                .amount(new BigDecimal("999.99"))
                .status("PROCESSING")
                .orderedAt(now)
                .build();

        assertThat(order.getOrderId()).isEqualTo("ORD-2001");
        assertThat(order.getCustomerName()).isEqualTo("Test User");
        assertThat(order.getItemName()).isEqualTo("Test Product");
        assertThat(order.getQuantity()).isEqualTo(3);
        assertThat(order.getAmount()).isEqualByComparingTo(new BigDecimal("999.99"));
        assertThat(order.getStatus()).isEqualTo("PROCESSING");
        assertThat(order.getOrderedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("No-args constructor creates empty entity")
    void noArgsConstructor_createsEmptyEntity() {
        Order order = new Order();
        assertThat(order.getOrderId()).isNull();
        assertThat(order.getCustomerName()).isNull();
    }

    @Test
    @DisplayName("Setters update fields correctly")
    void setters_updateFields() {
        Order order = new Order();
        order.setOrderId("ORD-3001");
        order.setCustomerName("Updated Name");
        order.setItemName("Updated Item");
        order.setQuantity(5);
        order.setAmount(new BigDecimal("500.00"));
        order.setStatus("SHIPPED");
        order.setOrderedAt(LocalDateTime.of(2026, 1, 1, 0, 0));

        assertThat(order.getOrderId()).isEqualTo("ORD-3001");
        assertThat(order.getCustomerName()).isEqualTo("Updated Name");
        assertThat(order.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Equals and hashCode work correctly")
    void equalsAndHashCode() {
        Order order1 = Order.builder().orderId("ORD-1001").customerName("A").itemName("B")
                .quantity(1).amount(BigDecimal.ONE).status("X")
                .orderedAt(LocalDateTime.now()).build();
        Order order2 = Order.builder().orderId("ORD-1001").customerName("A").itemName("B")
                .quantity(1).amount(BigDecimal.ONE).status("X")
                .orderedAt(order1.getOrderedAt()).build();

        assertThat(order1).isEqualTo(order2);
        assertThat(order1.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    @DisplayName("ToString contains field values")
    void toString_containsFields() {
        Order order = Order.builder().orderId("ORD-5001").customerName("Test").build();
        assertThat(order.toString()).contains("ORD-5001");
    }
}
