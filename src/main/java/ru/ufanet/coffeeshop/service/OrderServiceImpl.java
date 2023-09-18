package ru.ufanet.coffeeshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ufanet.coffeeshop.aggregate.OrderAggregate;
import ru.ufanet.coffeeshop.event.OrderDispatchedEvent;
import ru.ufanet.coffeeshop.event.OrderInProgressEvent;
import ru.ufanet.coffeeshop.event.OrderReadyEvent;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.handler.OrderEventHandler;
import ru.ufanet.coffeeshop.model.OrderTransferDto;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private OrderAggregate orderAggregate;

    @Override
    public OrderTransferDto publishEvent(OrderRegisteredEvent event) {
        return orderEventHandler.handleOrderRegisteredEvent(event);
    }

    @Override
    public void inProgressEvent(OrderInProgressEvent event) {
        orderEventHandler.handleOrderInProgressEvent(event);
    }

    @Override
    public void readyEvent(OrderReadyEvent event) {
        orderEventHandler.handleOrderReadyEvent(event);
    }

    @Override
    public void dispatchedEvent(OrderDispatchedEvent event) {
        orderEventHandler.handleOrderDispatchedEvent(event);
    }

    @Override
    public OrderTransferDto findOrder(Long orderId) {
        return orderAggregate.getOrderById(orderId);
    }
}
