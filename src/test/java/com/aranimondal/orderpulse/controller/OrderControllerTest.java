package com.aranimondal.orderpulse.controller;

import com.aranimondal.orderpulse.dto.OrderResponse;
import com.aranimondal.orderpulse.exception.OrderNotFoundException;
import com.aranimondal.orderpulse.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private OrderResponse sampleOrder() {
        return new OrderResponse(
                "ORD-1001",
                "Ananya Sharma",
                "Wireless Earbuds Pro",
                1,
                new BigDecimal("2499.00"),
                "DELIVERED",
                LocalDateTime.of(2026, 6, 10, 10, 15, 0)
        );
    }

    @Test
    @DisplayName("GET /api/v1/orders - returns all orders")
    void getAllOrders_returnsOrderList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId", is("ORD-1001")))
                .andExpect(jsonPath("$[0].customerName", is("Ananya Sharma")));
    }

    @Test
    @DisplayName("GET /api/v1/orders - returns empty list when no orders")
    void getAllOrders_returnsEmptyList() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} - returns single order")
    void getOrderById_returnsOrder() throws Exception {
        when(orderService.getOrderById("ORD-1001")).thenReturn(sampleOrder());

        mockMvc.perform(get("/api/v1/orders/ORD-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("ORD-1001")))
                .andExpect(jsonPath("$.amount", is(2499.00)));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} - returns 404 for non-existent order")
    void getOrderById_notFound() throws Exception {
        when(orderService.getOrderById("INVALID")).thenThrow(new OrderNotFoundException("INVALID"));

        mockMvc.perform(get("/api/v1/orders/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("INVALID")));
    }

    @Test
    @DisplayName("GET /api/v1/orders/health/info - returns service info")
    void healthInfo_returnsServiceDetails() throws Exception {
        mockMvc.perform(get("/api/v1/orders/health/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service", is("orderpulse-api")))
                .andExpect(jsonPath("$.status", is("UP")));
    }
}
