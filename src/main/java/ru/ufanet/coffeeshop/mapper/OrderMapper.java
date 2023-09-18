package ru.ufanet.coffeeshop.mapper;

import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderTransferDto;

public class OrderMapper {
    public static OrderTransferDto toTransferDto(Order order) {
        return new OrderTransferDto(
                order.getOrderId(),
                order.getClientId(),
                order.getEmployeeId(),
                order.getExpectedTime(),
                order.getProductId(),
                order.getProductCost(),
                order.getStatus(),
                order.getTimestamp(),
                order.getCause()
                );
    }
}
