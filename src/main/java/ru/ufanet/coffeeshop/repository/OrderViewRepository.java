package ru.ufanet.coffeeshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.coffeeshop.model.OrderView;

public interface OrderViewRepository extends JpaRepository<OrderView, Long> {
}