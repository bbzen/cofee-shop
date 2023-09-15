package ru.ufanet.coffeeshop.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRegisteredEvent {
    private final Long orderId;
    private final Long clientId;
    private final Long employeeId;
    private final LocalDateTime expectedTime;
    private final Long productId;
    private final Double productCost;
    private final LocalDateTime timestamp;
}