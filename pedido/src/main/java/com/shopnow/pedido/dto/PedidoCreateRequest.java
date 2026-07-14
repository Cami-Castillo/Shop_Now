package com.shopnow.pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PedidoCreateRequest(
        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId,
        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor o igual a 1")
        Integer cantidad
) {
}
