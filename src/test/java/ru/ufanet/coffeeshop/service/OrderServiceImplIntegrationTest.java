package ru.ufanet.coffeeshop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.model.OrderTransferDto;
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
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
    @SneakyThrows
    void findOrder() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        OrderRegisteredEvent eventNew = new OrderRegisteredEvent(21L, 31L, 41L, expectedTime, 51L, 10.0, LocalDateTime.now());
        OrderInProgressEvent eventInProgress = new OrderInProgressEvent(21L, 31L, LocalDateTime.now());
        OrderReadyEvent eventReady = new OrderReadyEvent(21L, 31L, LocalDateTime.now());
        OrderDispatchedEvent eventDispatched = new OrderDispatchedEvent(21L, 33L, LocalDateTime.now());

        orderService.publishEvent(eventNew);
        orderService.inProgressEvent(eventInProgress);
        orderService.readyEvent(eventReady);
        orderService.dispatchedEvent(eventDispatched);
        OrderTransferDto resultOrder = orderService.findOrder(eventNew.getOrderId());
        List<EventDto> resultEvents = mapper.readValue(resultOrder.getEvents(), new TypeReference<List<EventDto>>() {
        });

        assertEquals(eventNew.getOrderId(), resultOrder.getOrderId());
        assertEquals(eventNew.getClientId(), resultOrder.getClientId());
        assertEquals(eventNew.getExpectedTime().format(formatter), resultOrder.getExpectedTime().format(formatter));
        assertEquals(eventNew.getProductId(), resultOrder.getProductId());
        assertEquals(eventNew.getProductCost(), resultOrder.getProductCost());
        assertEquals(OrderStatus.NEW, resultOrder.getStatus());

        assertEquals(1L, resultEvents.get(0).getEventId());
        assertEquals(OrderStatus.NEW, resultEvents.get(0).getStatus());
        assertEquals(2L, resultEvents.get(1).getEventId());
        assertEquals(OrderStatus.IN_PROGRESS, resultEvents.get(1).getStatus());
        assertEquals(3L, resultEvents.get(2).getEventId());
        assertEquals(OrderStatus.READY, resultEvents.get(2).getStatus());
        assertEquals(4L, resultEvents.get(3).getEventId());
        assertEquals(OrderStatus.DISPATCHED, resultEvents.get(3).getStatus());
    }
}