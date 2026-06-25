package com.aranimondal.orderpulse.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderResponseTest {

    @Test
    @DisplayName("Record holds all fields correctly")
    void record_holdsAllFields() {
        LocalDateTime time = LocalDateTime.of(2026, 6, 15, 9, 0);
        OrderResponse response = new OrderResponse(
                "ORD-1001", "John Doe", "Laptop", 2,
                new BigDecimal("89999.00"), "DELIVERED", time
        );

        assertThat(response.orderId()).isEqualTo("ORD-1001");
        assertThat(response.customerName()).isEqualTo("John Doe");
        assertThat(response.itemName()).isEqualTo("Laptop");
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("89999.00"));
        assertThat(response.status()).isEqualTo("DELIVERED");
        assertThat(response.orderedAt()).isEqualTo(time);
    }

    @Test
    @DisplayName("Record equality works for same values")
    void record_equality() {
        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 0, 0);
        OrderResponse r1 = new OrderResponse("A", "B", "C", 1, BigDecimal.TEN, "X", time);
        OrderResponse r2 = new OrderResponse("A", "B", "C", 1, BigDecimal.TEN, "X", time);

        assertThat(r1).isEqualTo(r2);
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }

    @Test
    @DisplayName("Record inequality for different values")
    void record_inequality() {
        LocalDateTime time = LocalDateTime.now();
        OrderResponse r1 = new OrderResponse("ORD-1", "A", "B", 1, BigDecimal.ONE, "X", time);
        OrderResponse r2 = new OrderResponse("ORD-2", "A", "B", 1, BigDecimal.ONE, "X", time);

        assertThat(r1).isNotEqualTo(r2);
    }

    @Test
    @DisplayName("ToString contains field data")
    void toString_containsData() {
        OrderResponse response = new OrderResponse(
                "ORD-9999", "Test", "Item", 1, BigDecimal.ZERO, "NEW", LocalDateTime.now()
        );
        assertThat(response.toString()).contains("ORD-9999");
    }
}
