package com.aranimondal.orderpulse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OrderPulse API
 * <p>
 * Real-time order status lookup service for the storefront/ops dashboard.
 * Reads order records persisted by the pipeline and exposes them
 * over a simple REST API so support agents and customers can track an
 * order without hitting the database directly.
 */
@SpringBootApplication
public class OrderpulseApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderpulseApplication.class, args);
    }
}
