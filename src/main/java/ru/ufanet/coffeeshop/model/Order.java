package ru.ufanet.coffeeshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Order {
    @Id
    private Long orderId;
    private Long clientId;
    private Long employeeId;
    private LocalDateTime expectedTime;
    private Long productId;
    private Double productCost;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDateTime timestamp;

    public Order(Long orderId, Long clientId, Long employeeId, LocalDateTime expectedTime, Long productId, Double productCost, LocalDateTime timestamp) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.expectedTime = expectedTime;
        this.productId = productId;
        this.productCost = productCost;
        this.timestamp = timestamp;
    }
}