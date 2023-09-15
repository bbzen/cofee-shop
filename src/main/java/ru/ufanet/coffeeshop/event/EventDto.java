package ru.ufanet.coffeeshop.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.ufanet.coffeeshop.model.CancelCause;
import ru.ufanet.coffeeshop.model.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class EventDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;
    private Long orderId;
    private Long clientId;
    private Long employeeId;
    private LocalDateTime expectedTime;
    private Long productId;
    private Double productCost;
    private LocalDateTime timestamp;
    private OrderStatus status;
    private CancelCause cause;
}
