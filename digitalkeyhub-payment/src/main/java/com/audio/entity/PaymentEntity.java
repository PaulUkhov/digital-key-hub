package com.audio.entity;

import com.audio.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "stripe_payment_id", nullable = false)
    private String stripePaymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}