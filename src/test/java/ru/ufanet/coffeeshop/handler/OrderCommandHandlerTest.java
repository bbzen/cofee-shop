package ru.ufanet.coffeeshop.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ufanet.coffeeshop.command.CreateOrderCommand;
import ru.ufanet.coffeeshop.exception.OrderValidationException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCommandHandlerTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderCommandHandler orderCommandHandler = new OrderCommandHandler(orderRepository);
    @Captor
    private ArgumentCaptor<Order> orderArgumentCaptor;

    @Test
    public void handleCreateOrderCommandNormal() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        Order expectedOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class)))
                .thenReturn(expectedOrder);

        orderCommandHandler.handleCreateOrderCommand(createOrderCommand);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order orderResult = orderArgumentCaptor.getValue();

        verify(orderRepository).save(any(Order.class));
        assertEquals(expectedOrder.getOrderId(), orderResult.getOrderId());
        assertEquals(expectedOrder.getClientId(), orderResult.getClientId());
        assertEquals(expectedOrder.getEmployeeId(), orderResult.getEmployeeId());
        assertEquals(expectedOrder.getExpectedTime(), orderResult.getExpectedTime());
        assertEquals(expectedOrder.getProductId(), orderResult.getProductId());
        assertEquals(expectedOrder.getProductCost(), orderResult.getProductCost());
        assertEquals(OrderStatus.NEW, orderResult.getStatus());
        assertEquals(expectedOrder.getTimestamp(), orderResult.getTimestamp());
        assertEquals(expectedOrder.getCause(), orderResult.getCause());
    }

    @Test
    public void handleCreateOrderCommandFailOrderExists() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        CreateOrderCommand createOrderCommand = new CreateOrderCommand(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        Order expectedOrder = new Order(21L, 31L, 41L, expectedTime, 51L, 10.0, timestamp);
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(expectedOrder));

        Exception thrown = assertThrows(OrderValidationException.class, () -> orderCommandHandler.handleCreateOrderCommand(createOrderCommand));
        assertEquals("Заказ " + expectedOrder.getOrderId() + " уже существует.", thrown.getMessage());
    }
}