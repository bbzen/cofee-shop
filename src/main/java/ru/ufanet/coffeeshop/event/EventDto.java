package ru.ufanet.coffeeshop.event;

import lombok.*;
import ru.ufanet.coffeeshop.model.CancelCause;
import ru.ufanet.coffeeshop.model.OrderStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
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
    private OrderStatus status;
    private LocalDateTime timestamp;
    private CancelCause cause;

    public EventDto(Long orderId, Long clientId, Long employeeId, LocalDateTime expectedTime, Long productId, Double productCost, OrderStatus status, LocalDateTime timestamp, CancelCause cause) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.expectedTime = expectedTime;
        this.productId = productId;
        this.productCost = productCost;
        this.status = status;
        this.timestamp = timestamp;
        this.cause = cause;
    }
}
