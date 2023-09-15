package ru.ufanet.coffeeshop.command;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReadyOrderCommand {
    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime timestamp;
}