package com.aranimondal.orderpulse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
        String orderId,
        String customerName,
        String itemName,
        Integer quantity,
        BigDecimal amount,
        String status,
        LocalDateTime orderedAt
) {
}
