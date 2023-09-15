package ru.ufanet.coffeeshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.coffeeshop.event.EventDto;

import java.util.List;

public interface EventRepository extends JpaRepository<EventDto, Long> {
    List<EventDto> findAllByOrderIdOrderByEventId(Long orderId);
}
