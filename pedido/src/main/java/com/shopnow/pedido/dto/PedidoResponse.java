package com.shopnow.pedido.dto;

import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        String clienteCorreo,
        String descripcion,
        Integer cantidad,
        LocalDateTime createdAt
) {
}
