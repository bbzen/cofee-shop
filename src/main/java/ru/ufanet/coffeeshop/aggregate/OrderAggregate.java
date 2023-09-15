package ru.ufanet.coffeeshop.aggregate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class OrderAggregate {
    private OrderRepository orderRepository;
    private EventRepository eventRepository;

    public List<EventDto> getOrderEvents(Long orderId) {
        return eventRepository.findAllByOrderIdOrderByEventId(orderId);
    }
}
