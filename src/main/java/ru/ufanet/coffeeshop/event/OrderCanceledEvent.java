package ru.ufanet.coffeeshop.event;

import lombok.Data;
import ru.ufanet.coffeeshop.model.CancelCause;

import java.time.LocalDateTime;

@Data
public class OrderCanceledEvent implements OrderEvent {
    private final Long orderId;
    private final Long employeeId;
    private final CancelCause cause;
    private final LocalDateTime timestamp;
}