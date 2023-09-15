package ru.ufanet.coffeeshop.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderReadyEvent {
    private Long orderId;
    private Long employeeId;
    private LocalDateTime timestamp;
}