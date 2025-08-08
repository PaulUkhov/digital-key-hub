package com.audio.dto.request;

import com.audio.dto.response.OrderItemServiceResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateServiceRequest {
    @NotNull
    @Valid
    private List<OrderItemServiceResponse> items;
}
