package com.audio.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentServiceRequest {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
}