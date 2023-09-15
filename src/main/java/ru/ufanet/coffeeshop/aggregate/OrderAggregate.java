package ru.ufanet.coffeeshop.aggregate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class OrderAggregate {
    private OrderRepository orderRepository;
    private EventRepository eventRepository;

    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Заказ " + orderId + " не найден."));
        List<EventDto> events = getOrderEvents(orderId);
        events.stream().map(EventDto::getClientId).filter(Objects::nonNull).forEach(order::setClientId);
        events.stream().map(EventDto::getEmployeeId).filter(Objects::nonNull).forEach(order::setEmployeeId);
        events.stream().map(EventDto::getExpectedTime).filter(Objects::nonNull).forEach(order::setExpectedTime);
        events.stream().map(EventDto::getProductId).filter(Objects::nonNull).forEach(order::setProductId);
        events.stream().map(EventDto::getProductCost).filter(Objects::nonNull).forEach(order::setProductCost);
        events.stream().map(EventDto::getTimestamp).filter(Objects::nonNull).forEach(order::setTimestamp);
        events.stream().map(EventDto::getStatus).filter(Objects::nonNull).forEach(order::setStatus);
        events.stream().map(EventDto::getCause).filter(Objects::nonNull).forEach(order::setCause);

        return order;
    }

    private List<EventDto> getOrderEvents(Long orderId) {
        return eventRepository.findAllByOrderIdOrderByEventId(orderId);
    }
}
