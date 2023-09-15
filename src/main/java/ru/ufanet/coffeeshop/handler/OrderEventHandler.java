package ru.ufanet.coffeeshop.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ufanet.coffeeshop.command.CreateOrderCommand;
import ru.ufanet.coffeeshop.event.*;
import ru.ufanet.coffeeshop.exception.EventNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderNotFoundException;
import ru.ufanet.coffeeshop.exception.OrderStateException;
import ru.ufanet.coffeeshop.mapper.EventMapper;
import ru.ufanet.coffeeshop.model.Order;
import ru.ufanet.coffeeshop.model.OrderStatus;
import ru.ufanet.coffeeshop.repository.EventRepository;
import ru.ufanet.coffeeshop.repository.OrderRepository;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class OrderEventHandler {
    @Autowired
    private OrderCommandHandler orderCommandHandler;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EventRepository eventRepository;

    public Order handleOrderRegisteredEvent(OrderRegisteredEvent event) {
        log.info("Обрабатывается событие OrderRegisteredEvent: {}", event);

        CreateOrderCommand createOrderCommand = new CreateOrderCommand(
                event.getOrderId(),
                event.getClientId(),
                event.getEmployeeId(),
                event.getExpectedTime(),
                event.getProductId(),
                event.getProductCost(),
                event.getTimestamp());

        Order order = orderCommandHandler.handleCreateOrderCommand(createOrderCommand);
        eventRepository.save(EventMapper.toEventDto(event));
        return order;
    }

    public void handleOrderReadyEvent(OrderReadyEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);
        checkOrderExists(event.getOrderId());
        checkOrderStatus(event.getOrderId());

        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setStatus(OrderStatus.READY);
        eventRepository.save(eventDto);
    }

    public void handleOrderInProgressEvent(OrderInProgressEvent event) {
        log.info("Обрабатывается событие OrderReadyEvent: {}", event);
        checkOrderExists(event.getOrderId());
        checkOrderStatus(event.getOrderId());

        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setStatus(OrderStatus.IN_PROGRESS);
        eventRepository.save(eventDto);
    }

    public void handleOrderDispatchedEvent(OrderDispatchedEvent event) {
        log.info("Обрабатывается событие OrderDispatchedEvent: {}", event);
        checkOrderExists(event.getOrderId());
        checkOrderStatus(event.getOrderId());

        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setStatus(OrderStatus.DISPATCHED);
        eventRepository.save(eventDto);
    }

    public void handleOrderCanceledEvent(OrderCanceledEvent event) {
        log.info("Обрабатывается событие OrderCanceledEvent: {}", event);
        checkOrderExists(event.getOrderId());

        EventDto eventDto = EventMapper.toEventDto(event);
        eventDto.setStatus(OrderStatus.CANCELED);
        eventDto.setCause(event.getCause());
        eventRepository.save(eventDto);
    }

    private void checkOrderStatus(Long orderId) {
        List<EventDto> eventDtos = eventRepository.findAllByOrderIdOrderByEventId(orderId);
        if (eventDtos.isEmpty()) {
            throw new EventNotFoundException("Заказ " + orderId + " не найден.");
        }
        eventDtos.stream()
                .map(EventDto::getStatus)
                .filter(s -> s.equals(OrderStatus.DISPATCHED) || s.equals(OrderStatus.CANCELED))
                .findFirst()
                .ifPresent(s -> {throw new OrderStateException("Заказ отменен или завершен. Невозможно записать новое событие по заказу.");});

    }

    private void checkOrderExists(Long orderId) {
        if (orderRepository.findById(orderId).isEmpty()) {
            throw new OrderNotFoundException("Заказ " + orderId + " не найден.");
        }
    }
}