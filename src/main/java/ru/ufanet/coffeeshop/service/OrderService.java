package ru.ufanet.coffeeshop.service;

import org.springframework.stereotype.Service;
import ru.ufanet.coffeeshop.event.OrderEvent;
import ru.ufanet.coffeeshop.model.Order;

@Service
interface OrderService {

    void publishEvent(OrderEvent event);

    Order findOrder(int id);

}