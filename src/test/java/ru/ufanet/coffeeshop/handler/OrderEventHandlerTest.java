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
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    public void handleOrderRegisteredEvent() {
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
    public void handleOrderReadyEvent() {
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