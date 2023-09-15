package ru.ufanet.coffeeshop.aggregate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.command.*;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.exception.CommandValidationException;
import ru.ufanet.coffeeshop.exception.EventValidationException;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderValidationException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.OrderJpaRepository;

import java.util.List;

@Slf4j
@NoArgsConstructor
@Component
public class OrderAggregate {
    @Autowired
    private OrderJpaRepository orderRepository;

    public Order handleCreateOrderCommand(CreateOrderCommand command) {
        log.info("Выполняется InProgressOrderCommand: {}", command);
        if (orderRepository.findById(command.getOrderId()).isPresent()) {
            throw new OrderValidationException("Заказ " + command.getOrderId() + " уже существует.");
        }
        Order order = new Order(
                command.getOrderId(),
                command.getClientId(),
                command.getEmployeeId(),
                command.getExpectedTime(),
                command.getProductId(),
                command.getProductCost(),
                command.getTimestamp()
        );
        order.setStatus(OrderStatus.NEW);

        order = orderRepository.save(order);
        return order;
    }

    public Order handleInProgressOrderCommand(InProgressOrderCommand command) {
        log.info("Выполняется InProgressOrderCommand: {}", command);
        checkCommand(command);
        List<Order> orders = orderRepository.findAllByIdOrderByTimestamp(command.getOrderId());
        if (!orders.isEmpty()) {
            Order order = orders.get(orders.size() - 1);
            checkStatusOrder(order);
            order.setStatus(OrderStatus.IN_PROGRESS);
            orderRepository.save(order);
            return order;
        }
        throw new OrderNotFoundException("Заказ " + command.getOrderId() + " не найден.");
    }

    public Order handleReadyOrderCommand(ReadyOrderCommand command) {
        log.info("Выполняется ReadyOrderCommand: {}", command);
        checkCommand(command);
        List<Order> orders = orderRepository.findAllByIdOrderByTimestamp(command.getOrderId());
        if (!orders.isEmpty()) {
            Order order = orders.get(orders.size() - 1);
            checkStatusOrder(order);
            order.setStatus(OrderStatus.READY);
            orderRepository.save(order);
            return order;
        }
        throw new OrderNotFoundException("Заказ " + command.getOrderId() + " не найден.");
    }

    public Order handleDispatchedOrderCommand(DispatchedOrderCommand command) {
        log.info("Выполняется DispatchedOrderCommand: {}", command);
        checkCommand(command);
        List<Order> orders = orderRepository.findAllByIdOrderByTimestamp(command.getOrderId());
        if (!orders.isEmpty()) {
            Order order = orders.get(orders.size() - 1);
            if (order.getStatus() != OrderStatus.READY) {
                throw new OrderValidationException("Заказ " + command.getOrderId() + " не готов.");
            }
            order.setStatus(OrderStatus.READY);
            orderRepository.save(order);
            return order;
        }
        throw new OrderNotFoundException("Заказ " + command.getOrderId() + " не найден.");
    }

    public Order handleCancelOrderCommand(CancelOrderCommand command) {
        log.info("Выполняется CancelOrderCommand: {}", command);
        checkCommand(command);
        List<Order> orders = orderRepository.findAllByIdOrderByTimestamp(command.getOrderId());
        if (!orders.isEmpty()) {
            Order order = orders.get(orders.size() - 1);
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            return order;
        }
        throw new OrderNotFoundException("Заказ " + command.getOrderId() + " не найден.");
    }

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

    public void on(OrderReadyEvent event) {
        log.info("Добавляется OrderReadyEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.READY;
    }

    public void on(OrderInProgressEvent event) {
        log.info("Добавляется OrderInProgressEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.IN_PROGRESS;
    }

    public void on(OrderDispatchedEvent event) {
        log.info("Добавляется OrderDispatchedEvent: {}", event);
        checkEvent(event);
        status = OrderStatus.DISPATCHED;
    }

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

    private void checkCommand(OrderCommand orderCommand) {
        if (orderCommand.getEmployeeId() == null || orderCommand.getTimestamp() == null) {
            throw new CommandValidationException("Поля команды не могут быть null");
        }
    }

    private void checkStatusOrder(Order order) {
        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.DISPATCHED) {
            throw new OrderValidationException("Заказ " + order.getOrderId() + " отменен или закрыт.");
        }
    }
}