package ru.ufanet.coffeeshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrderTransferDto {
    private Long orderId;
    private Long clientId;
    private Long employeeId;
    private LocalDateTime expectedTime;
    private Long productId;
    private Double productCost;
    private OrderStatus status;
    private LocalDateTime timestamp;
    private CancelCause cause;
    private String events;

    public OrderTransferDto(Long orderId,
                            Long clientId,
                            Long employeeId,
                            LocalDateTime expectedTime,
                            Long productId, Double productCost,
                            OrderStatus status,
                            LocalDateTime timestamp,
                            CancelCause cause) {
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
