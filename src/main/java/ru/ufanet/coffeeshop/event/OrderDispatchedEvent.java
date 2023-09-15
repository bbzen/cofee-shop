package ru.ufanet.coffeeshop.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDispatchedEvent {
    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime timestamp;
}