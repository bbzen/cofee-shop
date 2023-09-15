package ru.ufanet.coffeeshop.service;

import org.springframework.stereotype.Service;
import ru.ufanet.coffeeshop.event.OrderEvent;
import ru.ufanet.coffeeshop.model.OrderView;

@Service
interface OrderService {

    void publishEvent(OrderEvent event);

    OrderView findOrder(int id);

}