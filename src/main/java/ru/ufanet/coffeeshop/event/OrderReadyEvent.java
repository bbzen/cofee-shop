package ru.ufanet.coffeeshop.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderReadyEvent implements OrderEvent {
    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime timestamp;
}