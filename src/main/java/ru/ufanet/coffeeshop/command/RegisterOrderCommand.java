package ru.ufanet.coffeeshop.command;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
public class RegisterOrderCommand {
    @TargetAggregateIdentifier
    private final Long orderId;
    private final Long clientId;
    private final Long employeeId;
    private final LocalDateTime expectedTime;
    private final Long productId;
    private final Double productCost;
    private final LocalDateTime timestamp;
}