package ru.ufanet.coffeeshop.aggregate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.mapper.OrderMapper;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderTransferDto;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class OrderAggregate {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EventRepository eventRepository;

    @SneakyThrows
    public OrderTransferDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Заказ " + orderId + " не найден."));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.writerWithDefaultPrettyPrinter();

        OrderTransferDto result = OrderMapper.toTransferDto(order);
        List<EventDto> events = getOrderEvents(orderId);
        result.setEvents(objectMapper.writeValueAsString(events));
        return result;
    }

    private List<EventDto> getOrderEvents(Long orderId) {
        return eventRepository.findAllByOrderIdOrderByEventId(orderId);
    }
}
