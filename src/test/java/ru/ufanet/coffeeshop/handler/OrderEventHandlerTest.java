package ru.ufanet.coffeeshop.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ufanet.coffeeshop.command.CreateOrderCommand;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.event.OrderReadyEvent;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.exception.EventNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderStateException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEventHandlerTest {
    @Mock
    private OrderCommandHandler orderCommandHandler;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private EventRepository eventRepository;
    @Captor
    private ArgumentCaptor<EventDto> eventDtoArgumentCaptor;
    @InjectMocks
    OrderEventHandler orderEventHandler = new OrderEventHandler(orderCommandHandler, orderRepository, eventRepository);

    @Test
    public void handleOrderRegisteredEventNormal() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        OrderRegisteredEvent srcEvent = new OrderRegisteredEvent(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        Order srcOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderCommandHandler.handleCreateOrderCommand(any(CreateOrderCommand.class)))
                .thenReturn(srcOrder);

        orderEventHandler.handleOrderRegisteredEvent(srcEvent);
        verify(eventRepository).save(eventDtoArgumentCaptor.capture());
        EventDto eventDto = eventDtoArgumentCaptor.getValue();

        assertEquals(srcEvent.getOrderId(), eventDto.getOrderId());
        assertEquals(srcEvent.getClientId(), eventDto.getClientId());
        assertEquals(srcEvent.getEmployeeId(), eventDto.getEmployeeId());
        assertEquals(srcEvent.getExpectedTime(), eventDto.getExpectedTime());
        assertEquals(srcEvent.getProductId(), eventDto.getProductId());
        assertEquals(srcEvent.getProductCost(), eventDto.getProductCost());
        assertEquals(OrderStatus.NEW, eventDto.getStatus());
        assertEquals(srcEvent.getTimestamp(), eventDto.getTimestamp());
    }

    @Test
    public void handleOrderReadyEventNormal() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        OrderReadyEvent srcEvent = new OrderReadyEvent(21L, 41L, timestamp);
        EventDto srcEventDto = new EventDto(11L, 21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.NEW, timestamp, null);
        Order srcOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(srcOrder));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(List.of(srcEventDto));

        orderEventHandler.handleOrderReadyEvent(srcEvent);
        verify(eventRepository).save(eventDtoArgumentCaptor.capture());
        EventDto eventDto = eventDtoArgumentCaptor.getValue();

        assertEquals(srcEvent.getOrderId(), eventDto.getOrderId());
        assertEquals(srcEvent.getEmployeeId(), eventDto.getEmployeeId());
        assertEquals(OrderStatus.READY, eventDto.getStatus());
        assertEquals(srcEvent.getTimestamp(), eventDto.getTimestamp());
    }

    @Test
    public void handleOrderReadyEventFailOrderNotFound() {
        LocalDateTime timestamp = LocalDateTime.now();
        OrderReadyEvent srcEvent = new OrderReadyEvent(21L, 41L, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        Exception thrown = assertThrows(OrderNotFoundException.class, () -> orderEventHandler.handleOrderReadyEvent(srcEvent));

        assertEquals("Заказ " + srcEvent.getOrderId() +" не найден.", thrown.getMessage());
    }

    @Test
    public void handleOrderReadyEventFailEventNotFound() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        OrderReadyEvent srcEvent = new OrderReadyEvent(21L, 41L, timestamp);
        Order srcOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(srcOrder));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(Collections.emptyList());

        Exception thrown = assertThrows(EventNotFoundException.class, () -> orderEventHandler.handleOrderReadyEvent(srcEvent));

        assertEquals("Заказ " + srcEvent.getOrderId() +" не найден.", thrown.getMessage());
    }

    @Test
    public void handleOrderReadyEventFailOrderDispatchedStatus() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        OrderReadyEvent srcEvent = new OrderReadyEvent(21L, 41L, timestamp);
        EventDto srcEventDto = new EventDto(11L, 21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.DISPATCHED, timestamp, null);
        Order srcOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(srcOrder));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(List.of(srcEventDto));

        Exception thrown = assertThrows(OrderStateException.class, () -> orderEventHandler.handleOrderReadyEvent(srcEvent));

        assertEquals("Заказ отменен или завершен. Невозможно записать новое событие по заказу.", thrown.getMessage());
    }

    @Test
    public void handleOrderReadyEventFailOrderCanceledStatus() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        OrderReadyEvent srcEvent = new OrderReadyEvent(21L, 41L, timestamp);
        EventDto srcEventDto = new EventDto(11L, 21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.CANCELED, timestamp, null);
        Order srcOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(srcOrder));
        when(eventRepository.findAllByOrderIdOrderByEventId(anyLong()))
                .thenReturn(List.of(srcEventDto));

        Exception thrown = assertThrows(OrderStateException.class, () -> orderEventHandler.handleOrderReadyEvent(srcEvent));

        assertEquals("Заказ отменен или завершен. Невозможно записать новое событие по заказу.", thrown.getMessage());
    }

    @Test
    public void handleOrderInProgressEvent() {
    }

    @Test
    public void handleOrderDispatchedEvent() {
    }

    @Test
    public void handleOrderCanceledEvent() {
    }
}