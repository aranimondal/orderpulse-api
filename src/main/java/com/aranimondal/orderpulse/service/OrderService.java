package com.aranimondal.orderpulse.service;

import com.aranimondal.orderpulse.dto.OrderResponse;
import com.aranimondal.orderpulse.entity.Order;
import com.aranimondal.orderpulse.exception.OrderNotFoundException;
import com.aranimondal.orderpulse.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders from database tier");
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        log.info("Fetching order [{}] from database tier", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerName(),
                order.getItemName(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus(),
                order.getOrderedAt()
        );
    }
}
