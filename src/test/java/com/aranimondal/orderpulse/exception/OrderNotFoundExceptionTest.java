package com.aranimondal.orderpulse.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderNotFoundExceptionTest {

    @Test
    @DisplayName("Exception message contains the order ID")
    void exceptionMessage_containsOrderId() {
        OrderNotFoundException ex = new OrderNotFoundException("ORD-5555");
        assertThat(ex.getMessage()).isEqualTo("Order not found with id: ORD-5555");
    }

    @Test
    @DisplayName("Exception is a RuntimeException")
    void exception_isRuntimeException() {
        OrderNotFoundException ex = new OrderNotFoundException("ORD-1");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
