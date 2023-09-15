package ru.ufanet.coffeeshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.coffeeshop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
