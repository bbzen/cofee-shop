package ru.ufanet.coffeeshop.model;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import ru.ufanet.coffeeshop.command.*;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.exception.EventValidationException;

import java.time.LocalDateTime;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Slf4j
@Aggregate
@NoArgsConstructor
public class Order {
    @AggregateIdentifier
    private Long orderId;
    private Long clientId;
    private Long employeeId;
    private LocalDateTime expectedTime;
    private Long productId;
    private Double productCost;
    private OrderStatus status;
    private LocalDateTime timestamp;

    @CommandHandler
    public Order(CreateOrderCommand currentCommand) {
        log.info("Выполняется CreateOrderCommand: {}", currentCommand);
        apply(new OrderRegisteredEvent(
                currentCommand.getOrderId(),
                currentCommand.getClientId(),
                currentCommand.getEmployeeId(),
                currentCommand.getExpectedTime(),
                currentCommand.getProductId(),
                currentCommand.getProductCost(),
                currentCommand.getTimestamp()));
    }

    @CommandHandler
    public void handle(InProgressOrderCommand currentCommand) {
        log.info("Выполняется InProgressOrderCommand: {}", currentCommand);
        apply(new OrderInProgressEvent(
                currentCommand.getOrderId(),
                currentCommand.getEmployeeId(),
                currentCommand.getTimestamp()));
    }

    @CommandHandler
    public void handle(ReadyOrderCommand currentCommand) {
        log.info("Выполняется ReadyOrderCommand: {}", currentCommand);
        apply(new OrderReadyEvent(
                currentCommand.getOrderId(),
                currentCommand.getEmployeeId(),
                currentCommand.getTimestamp()));
    }

    @CommandHandler
    public void handle(DispatchedOrderCommand currentCommand) {
        log.info("Выполняется DispatchedOrderCommand: {}", currentCommand);
        apply(new OrderDispatchedEvent(
                currentCommand.getOrderId(),
                currentCommand.getEmployeeId(),
                currentCommand.getTimestamp()));
    }

    @CommandHandler
    public void handle(CancelOrderCommand currentCommand) {
        log.info("Выполняется CancelOrderCommand: {}", currentCommand);
        apply(new OrderCanceledEvent(
                currentCommand.getOrderId(),
                currentCommand.getEmployeeId(),
                currentCommand.getCause(),
                currentCommand.getTimestamp()));
    }

    @EventSourcingHandler
    public void on(OrderRegisteredEvent event) {
        log.info("Добавляется OrderRegisteredEvent: {}", event);
        orderId = event.getOrderId();
        clientId = event.getClientId();
        employeeId = event.getEmployeeId();
        expectedTime = event.getExpectedTime();
        productId = event.getProductId();
        productCost = event.getProductCost();
        timestamp = event.getTimestamp();
        status = OrderStatus.NEW;
    }

    @EventSourcingHandler
    public void on(OrderReadyEvent event) {
        log.info("Добавляется OrderReadyEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.READY;
    }

    @EventSourcingHandler
    public void on(OrderInProgressEvent event) {
        log.info("Добавляется OrderInProgressEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.IN_PROGRESS;
    }

    @EventSourcingHandler
    public void on(OrderDispatchedEvent event) {
        log.info("Добавляется OrderDispatchedEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.DISPATCHED;
    }

    @EventSourcingHandler
    public void on(OrderCanceledEvent event) {
        log.info("Добавляется OrderCanceledEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.CANCELED;
    }

    private void checkEvent(OrderEvent event) {
        if (!this.orderId.equals(event.getOrderId()) && !this.employeeId.equals(event.getEmployeeId()) && !this.timestamp.equals(event.getTimestamp())) {
            log.debug("Переданное событие {} не соответствует заказу {}", event.getOrderId(), this.orderId);
            throw new EventValidationException("Переданное событие " + event.getOrderId() + " не соответствует заказу " + this.orderId);
        }
    }
}