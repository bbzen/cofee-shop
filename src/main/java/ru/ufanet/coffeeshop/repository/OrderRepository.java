package ru.ufanet.coffeeshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.coffeeshop.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByIdOrderByTimestamp(Long orderId);
}
