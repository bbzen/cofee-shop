package ru.ufanet.coffeeshop.mapper;

import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.event.OrderRegisteredEvent;

public class EventMapper {
    public static EventDto eventDto(OrderRegisteredEvent event) {
        return EventDto.builder()
                .orderId(event.getOrderId())
                .clientId(event.getClientId())
                .employeeId(event.getEmployeeId())
                .expectedTime(event.getExpectedTime())
                .productId(event.getProductId())
                .productCost(event.getProductCost())
                .timestamp(event.getTimestamp())
                .build();

    }
}
