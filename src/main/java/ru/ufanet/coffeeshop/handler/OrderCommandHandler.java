package ru.ufanet.coffeeshop.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.command.CreateOrderCommand;
import ru.ufanet.coffeeshop.exception.OrderValidationException;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.OrderRepository;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCommandHandler {
    @Autowired
    private OrderRepository orderRepository;

    public Order handleCreateOrderCommand(CreateOrderCommand command) {
        log.info("Выполняется InProgressOrderCommand: {}", command);
        if (orderRepository.findById(command.getOrderId()).isPresent()) {
            throw new OrderValidationException("Заказ " + command.getOrderId() + " уже существует.");
        }
        Order order = new Order(
                command.getOrderId(),
                command.getClientId(),
                command.getEmployeeId(),
                command.getExpectedTime(),
                command.getProductId(),
                command.getProductCost(),
                command.getTimestamp()
        );
        order.setStatus(OrderStatus.NEW);

        order = orderRepository.save(order);
        return order;
    }
}