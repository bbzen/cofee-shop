package ru.ufanet.coffeeshop.event;

import ru.ufanet.coffeeshop.model.CancelCause;

import java.time.LocalDateTime;

@dat
public class OrderEvent {
    private final Long orderId;
    private final Long clientId;
    private final Long employeeId;
    private final LocalDateTime expectedTime;
    private final Long productId;
    private final Double productCost;
    private final LocalDateTime timestamp;
    private final CancelCause cause;
}