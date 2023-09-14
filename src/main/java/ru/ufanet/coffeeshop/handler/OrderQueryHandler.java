package ru.ufanet.coffeeshop.handler;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.model.OrderView;
import ru.ufanet.coffeeshop.query.OrderQuery;
import ru.ufanet.coffeeshop.repository.OrderViewRepository;

import java.util.Optional;

@Slf4j
@Component
public class OrderQueryHandler {
    @Autowired
    OrderViewRepository orderViewRepository;

    @QueryHandler
    public OrderView handle(OrderQuery query) {
        log.info("Handling OrderQuery: {}", query);

        Optional<OrderView> order = orderViewRepository.findById(query.getOrderId());
        if (order.isEmpty())
            throw new OrderNotFoundException("Заказ не найден.");
        return order.get();
    }
}