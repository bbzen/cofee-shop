package ru.ufanet.coffeeshop.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderInProgressEvent {
    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime timestamp;
}