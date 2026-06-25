package com.aranimondal.orderpulse.service;

import com.aranimondal.orderpulse.dto.OrderResponse;
import com.aranimondal.orderpulse.entity.Order;
import com.aranimondal.orderpulse.exception.OrderNotFoundException;
import com.aranimondal.orderpulse.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order sampleEntity() {
        return Order.builder()
                .orderId("ORD-1001")
                .customerName("Ananya Sharma")
                .itemName("Wireless Earbuds Pro")
                .quantity(1)
                .amount(new BigDecimal("2499.00"))
                .status("DELIVERED")
                .orderedAt(LocalDateTime.of(2026, 6, 10, 10, 15, 0))
                .build();
    }

    @Test
    @DisplayName("getAllOrders - returns mapped list of responses")
    void getAllOrders_returnsMappedList() {
        when(orderRepository.findAll()).thenReturn(List.of(sampleEntity()));

        List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).orderId()).isEqualTo("ORD-1001");
        assertThat(result.get(0).customerName()).isEqualTo("Ananya Sharma");
        assertThat(result.get(0).amount()).isEqualByComparingTo(new BigDecimal("2499.00"));
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllOrders - returns empty list when no orders exist")
    void getAllOrders_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderResponse> result = orderService.getAllOrders();

        assertThat(result).isEmpty();
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getOrderById - returns order response when found")
    void getOrderById_returnsOrder() {
        when(orderRepository.findById("ORD-1001")).thenReturn(Optional.of(sampleEntity()));

        OrderResponse result = orderService.getOrderById("ORD-1001");

        assertThat(result.orderId()).isEqualTo("ORD-1001");
        assertThat(result.itemName()).isEqualTo("Wireless Earbuds Pro");
        assertThat(result.quantity()).isEqualTo(1);
        assertThat(result.status()).isEqualTo("DELIVERED");
        verify(orderRepository, times(1)).findById("ORD-1001");
    }

    @Test
    @DisplayName("getOrderById - throws OrderNotFoundException when not found")
    void getOrderById_throwsException() {
        when(orderRepository.findById("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById("INVALID"))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("INVALID");

        verify(orderRepository, times(1)).findById("INVALID");
    }
}
