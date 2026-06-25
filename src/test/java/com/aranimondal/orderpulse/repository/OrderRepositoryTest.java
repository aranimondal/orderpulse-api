package com.aranimondal.orderpulse.repository;

import com.aranimondal.orderpulse.entity.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Order createOrder(String id, String customer) {
        return Order.builder()
                .orderId(id)
                .customerName(customer)
                .itemName("Test Item")
                .quantity(1)
                .amount(new BigDecimal("100.00"))
                .status("DELIVERED")
                .orderedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("findAll returns all persisted orders")
    void findAll_returnsAllOrders() {
        entityManager.persist(createOrder("ORD-001", "Alice"));
        entityManager.persist(createOrder("ORD-002", "Bob"));
        entityManager.flush();

        List<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("findById returns order when exists")
    void findById_returnsOrder() {
        entityManager.persist(createOrder("ORD-100", "Charlie"));
        entityManager.flush();

        Optional<Order> found = orderRepository.findById("ORD-100");

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("Charlie");
    }

    @Test
    @DisplayName("findById returns empty when not exists")
    void findById_returnsEmpty() {
        Optional<Order> found = orderRepository.findById("NONEXISTENT");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("save persists a new order")
    void save_persistsOrder() {
        Order order = createOrder("ORD-NEW", "Dave");
        orderRepository.save(order);

        Order persisted = entityManager.find(Order.class, "ORD-NEW");
        assertThat(persisted).isNotNull();
        assertThat(persisted.getCustomerName()).isEqualTo("Dave");
    }
}
