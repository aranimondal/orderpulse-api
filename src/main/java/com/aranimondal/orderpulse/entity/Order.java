package com.aranimondal.orderpulse.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @Column(name = "order_id", length = 20)
    private String orderId;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "item_name", nullable = false, length = 200)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;
}
