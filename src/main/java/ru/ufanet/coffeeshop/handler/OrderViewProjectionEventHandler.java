package ru.ufanet.coffeeshop.handler;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderStateException;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.model.OrderView;
import ru.ufanet.coffeeshop.repository.OrderViewRepository;

@Slf4j
@Component
public class OrderViewProjectionEventHandler {
    @Autowired
    private OrderViewRepository orderViewRepository;

    @EventHandler
    public void handleOrderRegisteredEvent(OrderRegisteredEvent event) {
        log.info("Обрабатывается событие OrderRegisteredEvent: {}", event);

        OrderView orderView = new OrderView(event.getClientId(),
                event.getEmployeeId(),
                event.getExpectedTime(),
                event.getProductId(),
                event.getProductCost(),
                event.getTimestamp());
        orderView.setStatus(OrderStatus.NEW);
        orderViewRepository.save(orderView);
    }

    @EventHandler
    public void handleOrderReadyEvent(OrderReadyEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);

        OrderView orderView = getOrderView(event.getOrderId());
        checkOrderStatus(orderView);
        orderView.setStatus(OrderStatus.NEW);
        orderViewRepository.save(orderView);
    }

    @EventHandler
    public void handleOrderInProgressEvent(OrderInProgressEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);

        OrderView orderView = getOrderView(event.getOrderId());
        checkOrderStatus(orderView);
        orderView.setStatus(OrderStatus.IN_PROGRESS);
        orderViewRepository.save(orderView);
    }

    @EventHandler
    public void handleOrderDispatchedEvent(OrderDispatchedEvent event) {
        log.info("Обрабатывается событие OrderDispatchedEvent: {}", event);

        OrderView orderView = getOrderView(event.getOrderId());
        checkOrderStatus(orderView);
        orderView.setStatus(OrderStatus.DISPATCHED);
        orderViewRepository.save(orderView);
    }

    @EventHandler
    public void handleOrderCanceledEvent(OrderCanceledEvent event) {
        log.info("Обрабатывается событие OrderCanceledEvent: {}", event);

        OrderView orderView = getOrderView(event.getOrderId());
        checkOrderStatus(orderView);
        orderView.setStatus(OrderStatus.CANCELED);
        orderViewRepository.save(orderView);
    }

    private OrderView getOrderView(Long orderId) {
        return orderViewRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Заказ номер " + orderId + " не найден."));
    }

    private void checkOrderStatus(OrderView orderView) {
        if (orderView.getStatus() == OrderStatus.DISPATCHED || orderView.getStatus() == OrderStatus.CANCELED) {
            throw new OrderStateException("Заказ отменен или завершен. Невозможно записать новое событие по заказу.");
        }
    }
}