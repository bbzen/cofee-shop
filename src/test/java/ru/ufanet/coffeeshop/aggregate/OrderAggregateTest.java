package ru.ufanet.coffeeshop.aggregate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.model.OrderTransferDto;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderAggregateTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    OrderAggregate orderAggregate = new OrderAggregate(orderRepository, eventRepository);
    @Autowired
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private Order order;
    private LocalDateTime expectedTime;
    private LocalDateTime timestamp;

    private List<EventDto> events;

    @BeforeEach
    public void setUp() {
        events = new ArrayList<>();
        expectedTime = LocalDateTime.now().plusHours(1);
        timestamp = LocalDateTime.now();
        order = new Order(21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.NEW, timestamp, null);
    }

    @Test
    @SneakyThrows
    public void getOrderByIdNormal() {
        EventDto dto1 = new EventDto(1L, 21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.NEW, timestamp, null);
        EventDto dto2 = new EventDto(2L, 21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.IN_PROGRESS, timestamp, null);
        EventDto dto3 = new EventDto(3L, 21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.READY, timestamp, null);
        EventDto dto4 = new EventDto(4L, 21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.DISPATCHED, timestamp, null);
        events.add(dto1);
        events.add(dto2);
        events.add(dto3);
        events.add(dto4);
        Order expectedOrder = new Order(21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.NEW, timestamp, null);

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(order));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(events);

        OrderTransferDto orderDtoResult = orderAggregate.getOrderById(expectedOrder.getOrderId());
        List<EventDto> resultEvents = mapper.readValue(orderDtoResult.getEvents(), new TypeReference<List<EventDto>>() {
        });
        assertEquals(expectedOrder.getOrderId(), orderDtoResult.getOrderId());
        assertEquals(expectedOrder.getClientId(), orderDtoResult.getClientId());
        assertEquals(expectedOrder.getEmployeeId(), orderDtoResult.getEmployeeId());
        assertEquals(expectedOrder.getExpectedTime(), orderDtoResult.getExpectedTime());
        assertEquals(expectedOrder.getProductId(), orderDtoResult.getProductId());
        assertEquals(expectedOrder.getProductCost(), orderDtoResult.getProductCost());
        assertEquals(expectedOrder.getStatus(), orderDtoResult.getStatus());
        assertEquals(expectedOrder.getTimestamp(), orderDtoResult.getTimestamp());
        assertEquals(expectedOrder.getCause(), orderDtoResult.getCause());
        assertEquals(dto1.getEventId(), resultEvents.get(0).getEventId());
        assertEquals(dto2.getEventId(), resultEvents.get(1).getEventId());
        assertEquals(dto3.getEventId(), resultEvents.get(2).getEventId());
        assertEquals(dto4.getEventId(), resultEvents.get(3).getEventId());
    }

    @Test
    @SneakyThrows
    public void getOrderByIdNormalWithNulls() {
        EventDto dto1 = new EventDto(1L, 21L, null, null, null, null, null, null, null, null);
        events.add(dto1);

        Order expectedOrder = new Order(21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.NEW, timestamp, null);

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(order));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(events);

        OrderTransferDto orderDtoResult = orderAggregate.getOrderById(expectedOrder.getOrderId());
        List<EventDto> resultEvents = mapper.readValue(orderDtoResult.getEvents(), new TypeReference<List<EventDto>>() {
        });
        assertEquals(expectedOrder.getOrderId(), orderDtoResult.getOrderId());
        assertEquals(expectedOrder.getClientId(), orderDtoResult.getClientId());
        assertEquals(expectedOrder.getEmployeeId(), orderDtoResult.getEmployeeId());
        assertEquals(expectedOrder.getExpectedTime(), orderDtoResult.getExpectedTime());
        assertEquals(expectedOrder.getProductId(), orderDtoResult.getProductId());
        assertEquals(expectedOrder.getProductCost(), orderDtoResult.getProductCost());
        assertEquals(expectedOrder.getStatus(), orderDtoResult.getStatus());
        assertEquals(expectedOrder.getTimestamp(), orderDtoResult.getTimestamp());
        assertEquals(expectedOrder.getCause(), orderDtoResult.getCause());
        assertEquals(dto1.getEventId(), resultEvents.get(0).getEventId());
    }

    @Test
    @SneakyThrows
    public void getOrderByIdNormalChangeParams() {
        EventDto dto1 = new EventDto(1L, 21L, 63L, 73L, expectedTime.plusHours(1), 84L, 20.0, OrderStatus.DISPATCHED, timestamp.plusHours(1), null);
        events.add(dto1);

        Order expectedOrder = new Order(21L, 31L, 33L, expectedTime, 44L, 10.0, OrderStatus.NEW, timestamp, null);

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(order));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(events);

        OrderTransferDto orderDtoResult = orderAggregate.getOrderById(expectedOrder.getOrderId());
        List<EventDto> resultEvents = mapper.readValue(orderDtoResult.getEvents(), new TypeReference<List<EventDto>>() {
        });
        assertEquals(expectedOrder.getOrderId(), orderDtoResult.getOrderId());
        assertEquals(expectedOrder.getClientId(), orderDtoResult.getClientId());
        assertEquals(expectedOrder.getEmployeeId(), orderDtoResult.getEmployeeId());
        assertEquals(expectedOrder.getExpectedTime(), orderDtoResult.getExpectedTime());
        assertEquals(expectedOrder.getProductId(), orderDtoResult.getProductId());
        assertEquals(expectedOrder.getProductCost(), orderDtoResult.getProductCost());
        assertEquals(expectedOrder.getStatus(), orderDtoResult.getStatus());
        assertEquals(expectedOrder.getTimestamp(), orderDtoResult.getTimestamp());
        assertEquals(expectedOrder.getCause(), orderDtoResult.getCause());
        assertEquals(dto1.getEventId(), resultEvents.get(0).getEventId());
    }

    @Test
    public void getOrderByIdFailOrderId() {
        Exception thrown = assertThrows(OrderNotFoundException.class, () -> orderAggregate.getOrderById(order.getOrderId()));

        assertEquals("Заказ " + order.getOrderId() + " не найден.", thrown.getMessage());
    }
}