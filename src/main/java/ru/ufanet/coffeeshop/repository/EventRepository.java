package ru.ufanet.coffeeshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ufanet.coffeeshop.event.EventDto;

import java.util.List;

public interface EventRepository extends JpaRepository<EventDto, Long> {
    //todo remove query request if not used
//    @Query(value = "select * from events where orderId = ?1 order by eventId asc", nativeQuery = true)
    List<EventDto> findAllByOrderIdOrderByEventId(Long orderId);
}
