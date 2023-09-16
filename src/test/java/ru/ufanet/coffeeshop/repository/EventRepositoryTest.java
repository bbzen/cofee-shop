package ru.ufanet.coffeeshop.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.ufanet.coffeeshop.event.EventDto;
import ru.ufanet.coffeeshop.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    private EventRepository eventRepository;

    @Test
    public void findAllByOrderIdOrderByEventId() {
        LocalDateTime expectedTime = LocalDateTime.now().plusHours(1);
        LocalDateTime timestamp = LocalDateTime.now();
        EventDto eventDtoNew = new EventDto(21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.NEW, timestamp, null);
        EventDto eventDtoInProgress = new EventDto(21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.IN_PROGRESS, timestamp, null);
        EventDto eventDtoReady = new EventDto(21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.READY, timestamp, null);
        EventDto eventDtoDispatched = new EventDto(21L, 31L, 41L, expectedTime, 51L, 10.0, OrderStatus.DISPATCHED, timestamp, null);
        eventRepository.saveAll(List.of(eventDtoNew, eventDtoInProgress, eventDtoReady, eventDtoDispatched));

        List<EventDto> resultEventDtos = eventRepository.findAllByOrderIdOrderByEventId(eventDtoNew.getOrderId());
        assertEquals(4, resultEventDtos.size());
        assertEquals(eventDtoNew.getStatus(), resultEventDtos.get(0).getStatus());
        assertEquals(eventDtoNew.getOrderId(), resultEventDtos.get(0).getOrderId());
        assertEquals(1, resultEventDtos.get(0).getEventId());
        assertEquals(eventDtoInProgress.getStatus(), resultEventDtos.get(1).getStatus());
        assertEquals(eventDtoInProgress.getOrderId(), resultEventDtos.get(1).getOrderId());
        assertEquals(2, resultEventDtos.get(1).getEventId());
        assertEquals(eventDtoReady.getStatus(), resultEventDtos.get(2).getStatus());
        assertEquals(eventDtoReady.getOrderId(), resultEventDtos.get(2).getOrderId());
        assertEquals(3, resultEventDtos.get(2).getEventId());
    }
}