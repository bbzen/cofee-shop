package ru.ufanet.coffeeshop.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class OrderServiceImplIntegrationTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DirtiesContext
    void publishEvent() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        OrderRegisteredEvent srcEvent = new OrderRegisteredEvent(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);

        orderService.publishEvent(srcEvent);

        List<EventDto> resultDtos = eventRepository.findAll();
        EventDto resultDto = resultDtos.get(0);

        assertEquals(1, resultDtos.size());
        assertEquals(srcEvent.getOrderId(), resultDto.getOrderId());
        assertEquals(srcEvent.getClientId(), resultDto.getClientId());
        assertEquals(srcEvent.getEmployeeId(), resultDto.getEmployeeId());
        assertEquals(srcEvent.getExpectedTime().format(formatter), resultDto.getExpectedTime().format(formatter));
        assertEquals(srcEvent.getProductId(), resultDto.getProductId());
        assertEquals(srcEvent.getProductCost(), resultDto.getProductCost());
        assertEquals(OrderStatus.NEW, resultDto.getStatus());
        assertEquals(srcEvent.getTimestamp().format(formatter), resultDto.getTimestamp().format(formatter));
        assertNull(resultDto.getCause());

        List<Order> resultOrders = orderRepository.findAll();
        Order resultOrder = resultOrders.get(0);

        assertEquals(1, resultOrders.size());
        assertEquals(srcEvent.getOrderId(), resultOrder.getOrderId());
        assertEquals(srcEvent.getClientId(), resultOrder.getClientId());
        assertEquals(srcEvent.getEmployeeId(), resultOrder.getEmployeeId());
        assertEquals(srcEvent.getExpectedTime().format(formatter), resultOrder.getExpectedTime().format(formatter));
        assertEquals(srcEvent.getProductId(), resultOrder.getProductId());
        assertEquals(srcEvent.getProductCost(), resultOrder.getProductCost());
        assertEquals(OrderStatus.NEW, resultOrder.getStatus());
        assertEquals(srcEvent.getTimestamp().format(formatter), resultOrder.getTimestamp().format(formatter));
        assertNull(resultOrder.getCause());
    }

    @Test
    @DirtiesContext
    void findOrder() {

    }
}