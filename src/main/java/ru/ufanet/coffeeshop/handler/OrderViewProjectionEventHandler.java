package ru.ufanet.coffeeshop.handler;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.model.OrderView;
import ru.ufanet.coffeeshop.repository.OrderViewRepository;

@Slf4j
@Component
public class OrderViewProjectionEventHandler {
    @Autowired
    private OrderViewRepository orderViewRepository;

    @EventHandler
    public void orderRegisteredEventHandler(OrderRegisteredEvent event) {
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
}