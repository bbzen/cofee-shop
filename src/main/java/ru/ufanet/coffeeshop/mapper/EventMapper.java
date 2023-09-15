package ru.ufanet.coffeeshop.mapper;

import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.model.OrderStatus;

public class EventMapper {
    public static EventDto toEventDto(OrderRegisteredEvent event) {
        return EventDto.builder()
                .orderId(event.getOrderId())
                .clientId(event.getClientId())
                .employeeId(event.getEmployeeId())
                .expectedTime(event.getExpectedTime())
                .productId(event.getProductId())
                .productCost(event.getProductCost())
                .timestamp(event.getTimestamp())
                .status(OrderStatus.NEW)
                .build();
    }

    public static EventDto toEventDto(OrderReadyEvent event) {
        return EventDto.builder()
                .orderId(event.getOrderId())
                .employeeId(event.getEmployeeId())
                .timestamp(event.getTimestamp())
                .status(OrderStatus.READY)
                .build();
    }

    public static EventDto toEventDto(OrderInProgressEvent event) {
        return EventDto.builder()
                .orderId(event.getOrderId())
                .employeeId(event.getEmployeeId())
                .timestamp(event.getTimestamp())
                .status(OrderStatus.IN_PROGRESS)
                .build();
    }

    public static EventDto toEventDto(OrderDispatchedEvent event) {
        return EventDto.builder()
                .orderId(event.getOrderId())
                .employeeId(event.getEmployeeId())
                .timestamp(event.getTimestamp())
                .status(OrderStatus.DISPATCHED)
                .build();
    }

    public static EventDto toEventDto(OrderCanceledEvent event) {
        return EventDto.builder()
                .orderId(event.getOrderId())
                .employeeId(event.getEmployeeId())
                .timestamp(event.getTimestamp())
                .status(OrderStatus.CANCELED)
                .build();
    }
}
