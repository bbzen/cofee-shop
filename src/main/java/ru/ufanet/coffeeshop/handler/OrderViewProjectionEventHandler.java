package ru.ufanet.coffeeshop.handler;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderStateException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.OrderJpaRepository;

@Slf4j
@Component
public class OrderViewProjectionEventHandler {
    @Autowired
    private OrderJpaRepository orderViewJpaRepository;

    @EventHandler
    public void handleOrderRegisteredEvent(OrderRegisteredEvent event) {
        log.info("Обрабатывается событие OrderRegisteredEvent: {}", event);

        Order order = new Order(event.getClientId(),
                event.getEmployeeId(),
                event.getExpectedTime(),
                event.getProductId(),
                event.getProductCost(),
                event.getTimestamp());
        order.setStatus(OrderStatus.NEW);
        orderViewJpaRepository.save(order);
    }

    @EventHandler
    public void handleOrderReadyEvent(OrderReadyEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.NEW);
        orderViewJpaRepository.save(order);
    }

    @EventHandler
    public void handleOrderInProgressEvent(OrderInProgressEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderViewJpaRepository.save(order);
    }

    @EventHandler
    public void handleOrderDispatchedEvent(OrderDispatchedEvent event) {
        log.info("Обрабатывается событие OrderDispatchedEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.DISPATCHED);
        orderViewJpaRepository.save(order);
    }

    @EventHandler
    public void handleOrderCanceledEvent(OrderCanceledEvent event) {
        log.info("Обрабатывается событие OrderCanceledEvent: {}", event);

        Order order = getOrderView(event.getOrderId());
        checkOrderStatus(order);
        order.setStatus(OrderStatus.CANCELED);
        orderViewJpaRepository.save(order);
    }

    private Order getOrderView(Long orderId) {
        return orderViewJpaRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Заказ номер " + orderId + " не найден."));
    }

    private void checkOrderStatus(Order order) {
        if (order.getStatus() == OrderStatus.DISPATCHED || order.getStatus() == OrderStatus.CANCELED) {
            throw new OrderStateException("Заказ отменен или завершен. Невозможно записать новое событие по заказу.");
        }
    }
}