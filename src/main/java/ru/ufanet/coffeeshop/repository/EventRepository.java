package ru.ufanet.coffeeshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.coffeeshop.event.EventDto;

public interface EventRepository extends JpaRepository<EventDto, Long> {
}
