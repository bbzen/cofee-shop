package ru.ufanet.coffeeshop.command;

import java.time.LocalDateTime;

public interface OrderCommand {
    Long getOrderId();
    Long getEmployeeId();
    LocalDateTime getTimestamp();
}
