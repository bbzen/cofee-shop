package ru.ufanet.coffeeshop.event;

import java.time.LocalDateTime;

public interface OrderEvent {
    Long getOrderId();
    Long getEmployeeId();
    LocalDateTime getTimestamp();
}