package ru.ufanet.coffeeshop.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.command.CreateOrderCommand;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderStateException;
import ru.ufanet.coffeeshop.mapper.EventMapper;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.EventRepository;

@Slf4j
@Component
public class OrderEventHandler {
    @Autowired
    private OrderCommandHandler orderCommandHandler;
    @Autowired
    private EventRepository eventRepository;

    public Order handleOrderRegisteredEvent(OrderRegisteredEvent event) {
        log.info("Обрабатывается событие OrderRegisteredEvent: {}", event);

        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                event.getOrderId(),
                event.getClientId(),
                event.getEmployeeId(),
                event.getExpectedTime(),
                event.getProductId(),
                event.getProductCost(),
                event.getTimestamp());
        Order order = orderCommandHandler.handleCreateOrderCommand(createOrderCommand);
        eventRepository.save(EventMapper.eventDto(event));
        return order;
    }

    public void handleOrderReadyEvent(OrderReadyEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);

        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                event.getOrderId(),
                event.getClientId(),
                event.getEmployeeId(),
                event.getExpectedTime(),
                event.getProductId(),
                event.getProductCost(),
                event.getTimestamp());
        Order order = orderCommandHandler.handleCreateOrderCommand(createOrderCommand);
        eventRepository.save(EventMapper.eventDto(event));
    }

    public void handleOrderInProgressEvent(OrderInProgressEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.save(order);
    }

    public void handleOrderDispatchedEvent(OrderDispatchedEvent event) {
        log.info("Обрабатывается событие OrderDispatchedEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.DISPATCHED);
        orderRepository.save(order);
    }

    public void handleOrderCanceledEvent(OrderCanceledEvent event) {
        log.info("Обрабатывается событие OrderCanceledEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    private Order getOrderView(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Заказ номер " + orderId + " не найден."));
    }

    private void checkOrderStatus(Order order) {
        if (order.getStatus() == OrderStatus.DISPATCHED || order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderStateException("Заказ отменен или завершен. Невозможно записать новое событие по заказу.");
        }
    }
}