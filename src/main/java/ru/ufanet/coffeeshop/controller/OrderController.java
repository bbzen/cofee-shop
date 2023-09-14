package ru.ufanet.coffeeshop.controller;

import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.ufanet.coffeeshop.command.CreateOrderCommand;
import ru.ufanet.coffeeshop.model.OrderInputDto;
import ru.ufanet.coffeeshop.model.OrderView;
import ru.ufanet.coffeeshop.query.OrderQuery;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("/{orderId}")
    public OrderView findOrder(@PathVariable Long orderId) {
        return queryGateway.query(new OrderQuery(orderId), OrderView.class).join();
    }

    @PostMapping
    public void createOrder(@RequestBody OrderInputDto dto) {
        commandGateway.sendAndWait(
                new CreateOrderCommand(dto.getOrderId(),
                        dto.getClientId(),
                        dto.getEmployeeId(),
                        dto.getExpectedTime(),
                        dto.getProductId(),
                        dto.getProductCost(),
                        dto.getTimestamp()));
    }
}
