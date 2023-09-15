package ru.ufanet.coffeeshop.command;

import lombok.Data;
import ru.ufanet.coffeeshop.model.CancelCause;

import java.time.LocalDateTime;

@Data
public class CancelOrderCommand implements OrderCommand {
    private final Long orderId;
    private final Long employeeId;
    private final CancelCause cause;
    private final LocalDateTime timestamp;
}