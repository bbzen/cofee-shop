package ru.ufanet.coffeeshop.service;

import org.springframework.stereotype.Service;
import ru.ufanet.coffeeshop.event.OrderDispatchedEvent;
import ru.ufanet.coffeeshop.event.OrderInProgressEvent;
import ru.ufanet.coffeeshop.event.OrderReadyEvent;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.model.Order;

@Service
interface OrderService {
    Order publishEvent(OrderRegisteredEvent event);

    void inProgressEvent(OrderInProgressEvent event);

    void readyEvent(OrderReadyEvent event);

    void dispatchedEvent(OrderDispatchedEvent event);

    Order findOrder(Long id);

}