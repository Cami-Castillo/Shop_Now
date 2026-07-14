package com.shopnow.cliente.dto;

import java.time.LocalDateTime;

public record PedidoRemotoResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        String clienteCorreo,
        String descripcion,
        Integer cantidad,
        LocalDateTime createdAt
) {
}
