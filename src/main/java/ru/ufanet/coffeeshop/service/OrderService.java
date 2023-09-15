package ru.ufanet.coffeeshop.service;

import org.springframework.stereotype.Service;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.model.Order;

@Service
interface OrderService {
    Order publishEvent(OrderRegisteredEvent event);

    Order findOrder(int id);

}