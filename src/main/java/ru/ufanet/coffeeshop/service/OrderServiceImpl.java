package ru.ufanet.coffeeshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.ufanet.coffeeshop.aggregate.OrderAggregate;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.handler.OrderEventHandler;
import ru.ufanet.coffeeshop.model.Order;

public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderEventHandler orderEventHandler;
    @Autowired
    private OrderAggregate orderAggregate;

    @Override
    public Order publishEvent(OrderRegisteredEvent event) {
        return null;
    }

    @Override
    public Order findOrder(int id) {
        return null;
    }
}
