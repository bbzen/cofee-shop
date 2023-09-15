package ru.ufanet.coffeeshop.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderReadyEvent {
    private Long orderId;
    private Long employeeId;
    private LocalDateTime timestamp;
}