package ru.ufanet.coffeeshop.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ufanet.coffeeshop.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderCommandHandlerTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderCommandHandler orderCommandHandler = new OrderCommandHandler(orderRepository);

    @Test
    public void handleCreateOrderCommand() {
    }
}