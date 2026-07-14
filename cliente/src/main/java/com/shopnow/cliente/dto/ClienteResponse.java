package com.shopnow.cliente.dto;

import com.shopnow.cliente.model.Role;

public record ClienteResponse(
        Integer id,
        String nombre,
        String correo,
        String direccion,
        Role role
) {
}
