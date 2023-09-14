package ru.ufanet.coffeeshop.command;

import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@Data
public class DispatchedOrderCommand {
    @TargetAggregateIdentifier
    private final Long orderId;
    private final Long employeeId;
    private final LocalDateTime timestamp;
}