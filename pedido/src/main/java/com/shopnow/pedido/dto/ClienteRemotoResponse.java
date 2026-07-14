package com.shopnow.pedido.dto;

public record ClienteRemotoResponse(
        Integer id,
        String nombre,
        String correo,
        String direccion,
        String role
) {
}
